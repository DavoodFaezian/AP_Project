import APIServer.Request;
import APIServer.Response;
import APIServer.Server;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class MultiUserTests {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 1234;

    private static final int TOTAL_USERS = 1000;
    private static final int PHOTOS_PER_USER = 5;

    private final Gson gson = new Gson();

    @Test
    void multipleUsersLoginUploadAddAndGetPhotos() throws Exception {

        /*
         * 1. Start the server
         */
        Thread serverThread = new Thread(Server::main);
        serverThread.setDaemon(true);
        serverThread.start();

        // Give the server time to bind
        Thread.sleep(1000);

        /*
         * Each task represents one user.
         *
         * Each user keeps ONE TCP connection open for their
         * entire sequence of requests.
         */
        ExecutorService executor =
                Executors.newFixedThreadPool(500);

        List<Future<Void>> futures = new ArrayList<>();

        String projectPath = System.getProperty("user.dir");

        String photoBase64 = convertPhotoToBase64(
                projectPath + File.separator + "testimage.bin"
        );

        for (int i = 0; i < TOTAL_USERS; i++) {

            final int userIndex = i;

            Callable<Void> task = () -> {

                /*
                 * Stagger user startup over 5 seconds.
                 */
                Thread.sleep(
                        (long) (Math.random() * 5000)
                );

                String username = "user" + userIndex;
                String password = "Password@" + userIndex;

                /*
                 * =========================================================
                 * ONE CONNECTION PER USER
                 * =========================================================
                 */
                try (
                        Socket socket = new Socket(HOST, PORT);

                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        socket.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );

                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(
                                        socket.getOutputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
                ) {

                    // =====================================================
                    // 1. Sign Up
                    // =====================================================

                    JsonObject loginPayload = new JsonObject();

                    loginPayload.addProperty(
                            "userName",
                            username
                    );

                    loginPayload.addProperty(
                            "password",
                            password
                    );

                    loginPayload.addProperty(
                            "repeatedPassword",
                            password
                    );

                    Response loginResponse = sendRequest(
                            "User/signUp",
                            loginPayload,
                            reader,
                            writer
                    );

                    assertSuccessful(
                            loginResponse,
                            "Login failed for " + username
                    );

                    String sessionId = getRequiredString(
                            getPayload(loginResponse),
                            "id"
                    );


                    // =====================================================
                    // 2. Create Album
                    // =====================================================

                    JsonObject createAlbumPayload =
                            new JsonObject();

                    createAlbumPayload.addProperty(
                            "sessionId",
                            sessionId
                    );

                    createAlbumPayload.addProperty(
                            "name",
                            "album-" + userIndex
                    );

                    Response createAlbumResponse = sendRequest(
                            "Album/addAlbum",
                            createAlbumPayload,
                            reader,
                            writer
                    );

                    assertSuccessful(
                            createAlbumResponse,
                            "Create album failed for " + username
                    );

                    String albumId = getRequiredString(
                            getPayload(createAlbumResponse),
                            "id"
                    );


                    // =====================================================
                    // 3-6. Upload and process 10 photos
                    // =====================================================

                    for (int photoIndex = 0;
                         photoIndex < PHOTOS_PER_USER;
                         photoIndex++) {

                        // -------------------------------------------------
                        // 3. Upload Photo Bytes
                        // -------------------------------------------------

                        JsonObject uploadPayload =
                                new JsonObject();

                        uploadPayload.addProperty(
                                "sessionId",
                                sessionId
                        );

                        uploadPayload.addProperty(
                                "photoData",
                                photoBase64
                        );

                        Response uploadResponse = sendRequest(
                                "Photo/uploadPhoto",
                                uploadPayload,
                                reader,
                                writer
                        );

                        assertSuccessful(
                                uploadResponse,
                                "Photo upload failed for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );

                        String uploadedPhotoId =
                                getRequiredString(
                                        getPayload(uploadResponse),
                                        "id"
                                );


                        // -------------------------------------------------
                        // 4. Add Photo Metadata
                        // -------------------------------------------------

                        JsonObject addPhotoPayload =
                                new JsonObject();

                        addPhotoPayload.addProperty(
                                "sessionId",
                                sessionId
                        );

                        addPhotoPayload.addProperty(
                                "photoName",
                                uploadedPhotoId
                        );

                        addPhotoPayload.addProperty(
                                "title",
                                "Test photo "
                                        + userIndex
                                        + "-"
                                        + photoIndex
                        );

                        JsonArray albumIds =
                                new JsonArray();

                        albumIds.add(albumId);

                        addPhotoPayload.add(
                                "albumIds",
                                albumIds
                        );

                        JsonArray tags =
                                new JsonArray();

                        tags.add("test");
                        tags.add("multi-user");

                        addPhotoPayload.add(
                                "tags",
                                tags
                        );

                        addPhotoPayload.addProperty(
                                "caption",
                                "Concurrent upload test"
                        );

                        addPhotoPayload.addProperty(
                                "favorable",
                                true
                        );

                        Response addPhotoResponse =
                                sendRequest(
                                        "Photo/addPhoto",
                                        addPhotoPayload,
                                        reader,
                                        writer
                                );

                        assertSuccessful(
                                addPhotoResponse,
                                "Adding photo failed for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );

                        String photoId =
                                getRequiredString(
                                        getPayload(addPhotoResponse),
                                        "id"
                                );


                        // -------------------------------------------------
                        // 5. Get Photo Metadata
                        // -------------------------------------------------

                        JsonObject getPhotoPayload =
                                new JsonObject();

                        getPhotoPayload.addProperty(
                                "sessionId",
                                sessionId
                        );

                        getPhotoPayload.addProperty(
                                "photoId",
                                photoId
                        );

                        Response getPhotoResponse =
                                sendRequest(
                                        "Photo/getPhotoById",
                                        getPhotoPayload,
                                        reader,
                                        writer
                                );

                        assertSuccessful(
                                getPhotoResponse,
                                "Getting photo metadata failed for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );

                        assertNotNull(
                                getPhotoResponse.getPayload(),
                                "Photo payload is null for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );


                        // -------------------------------------------------
                        // 6. Get Photo Bytes
                        // -------------------------------------------------

                        JsonObject getBytesPayload =
                                new JsonObject();

                        getBytesPayload.addProperty(
                                "sessionId",
                                sessionId
                        );

                        getBytesPayload.addProperty(
                                "photoId",
                                uploadedPhotoId
                        );

                        Response getBytesResponse =
                                sendRequest(
                                        "Photo/getPhotoBytes",
                                        getBytesPayload,
                                        reader,
                                        writer
                                );

                        assertSuccessful(
                                getBytesResponse,
                                "Getting photo bytes failed for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );

                        String returnedBase64 =
                                getRequiredString(
                                        getPayload(getBytesResponse),
                                        "photoData"
                                );

                        assertEquals(
                                photoBase64,
                                returnedBase64,
                                "Returned photo bytes do not match "
                                        + "uploaded bytes for "
                                        + username
                                        + ", photo "
                                        + photoIndex
                        );
                    }

                    /*
                     * Socket closes here.
                     *
                     * One user = ONE connection.
                     *
                     * The user performed:
                     *
                     *   1  sign up
                     *   1  create album
                     *  10  upload photos
                     *  10  add photo metadata
                     *  10  get photo metadata
                     *  10  get photo bytes
                     *
                     * Total = 42 requests using ONE connection.
                     */
                }

                return null;
            };

            futures.add(
                    executor.submit(task)
            );
        }

        /*
         * Wait for every user to finish.
         */
        try {

            for (Future<Void> future : futures) {
                future.get(
                        10,
                        TimeUnit.MINUTES
                );
            }

        } finally {

            executor.shutdownNow();
        }
    }


    /**
     * Sends a request using an existing TCP connection.
     *
     * This method does NOT create or close the socket.
     */
    private Response sendRequest(
            String actionName,
            JsonObject payload,
            BufferedReader reader,
            BufferedWriter writer
    ) throws Exception {

        Request request =
                new Request(
                        actionName,
                        payload
                );

        String requestJson =
                gson.toJson(request);

        writer.write(requestJson);
        writer.newLine();
        writer.flush();

        String responseJson =
                reader.readLine();

        assertNotNull(
                responseJson,
                "The server returned an empty response for "
                        + actionName
        );

        return gson.fromJson(
                responseJson,
                Response.class
        );
    }


    private String convertPhotoToBase64(
            String filePath
    ) throws Exception {

        byte[] photoBytes =
                Files.readAllBytes(
                        Path.of(filePath)
                );

        return Base64.getEncoder()
                .encodeToString(photoBytes);
    }


    private JsonObject getPayload(
            Response response
    ) {

        if (response.getPayload() != null) {
            return response.getPayload();
        }

        fail(
                "Both payload and data are null. Server message: "
                        + response.getMessage()
        );

        return null;
    }


    private String getRequiredString(
            JsonObject jsonObject,
            String fieldName
    ) {

        assertNotNull(
                jsonObject,
                "JSON object is null while reading "
                        + fieldName
        );

        assertTrue(
                jsonObject.has(fieldName),
                "Missing field '"
                        + fieldName
                        + "' in response: "
                        + jsonObject
        );

        assertFalse(
                jsonObject.get(fieldName).isJsonNull(),
                "Field '"
                        + fieldName
                        + "' is null"
        );

        return jsonObject
                .get(fieldName)
                .getAsString();
    }


    private void assertSuccessful(
            Response response,
            String failureMessage
    ) {

        assertNotNull(
                response,
                failureMessage
                        + ": response is null"
        );

        String status =
                response.getStatus();

        boolean successful =
                "200".equals(status)
                        || "SUCCESS".equalsIgnoreCase(status);

        assertTrue(
                successful,
                failureMessage
                        + ". Status: "
                        + status
                        + ", message: "
                        + response.getMessage()
        );
    }
}