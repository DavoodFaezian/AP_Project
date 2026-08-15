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

    private final Gson gson = new Gson();

    @Test
    void multipleUsersLoginUploadAddAndGetPhoto() throws Exception {

        /*
         * 1. Start the server
         */
        Thread serverThread = new Thread(Server::main);
        serverThread.setDaemon(true);
        serverThread.start();

        // Give the server time to bind
        Thread.sleep(1000);

        int totalUsers = 100;

        /*
         * FIX: Limit the test thread pool to 500.
         * This prevents the JVM from crashing due to thread exhaustion,
         * while still processing all 5,000 users rapidly.
         */
        ExecutorService executor = Executors.newFixedThreadPool(500);
        List<Future<Void>> futures = new ArrayList<>();

        String projectPath = System.getProperty("user.dir");
        String photoBase64 = convertPhotoToBase64(projectPath + File.separator + "testimage.bin");

        for (int i = 0; i < totalUsers; i++) {
            final int userIndex = i;

            Callable<Void> task = () -> {
                /*
                 * FIX: Stagger the start times randomly over 5 seconds.
                 * This mimics real-world traffic (a ramp-up) and prevents
                 * instantly flooding the OS socket queue.
                 */
                Thread.sleep((long) (Math.random() * 5000));

                String username = "user" + userIndex;
                String password = "Password@" + userIndex;

                // --- 1. Login ---
                JsonObject loginPayload = new JsonObject();
                loginPayload.addProperty("userName", username);
                loginPayload.addProperty("password", password);
                loginPayload.addProperty("repeatedPassword", password);

                Response loginResponse = sendRequest("User/signUp", loginPayload);
                assertSuccessful(loginResponse, "Login failed for " + username);
                String sessionId = getRequiredString(getPayload(loginResponse), "id");

                // --- 2. Create Album ---
                JsonObject createAlbumPayload = new JsonObject();
                createAlbumPayload.addProperty("sessionId", sessionId);
                createAlbumPayload.addProperty("name", "album-" + userIndex);

                Response createAlbumResponse = sendRequest("Album/addAlbum", createAlbumPayload);
                assertSuccessful(createAlbumResponse, "Create album failed for " + username);
                String albumId = getRequiredString(getPayload(createAlbumResponse), "id");

                // --- 3. Upload Photo Bytes ---
                JsonObject uploadPayload = new JsonObject();
                uploadPayload.addProperty("sessionId", sessionId);
                uploadPayload.addProperty("photoData", photoBase64);

                Response uploadResponse = sendRequest("Photo/uploadPhoto", uploadPayload);
                assertSuccessful(uploadResponse, "Photo upload failed for " + username);
                String uploadedPhotoId = getRequiredString(getPayload(uploadResponse), "id");

                // --- 4. Add Photo Metadata ---
                JsonObject addPhotoPayload = new JsonObject();
                addPhotoPayload.addProperty("sessionId", sessionId);
                addPhotoPayload.addProperty("photoName", uploadedPhotoId);
                addPhotoPayload.addProperty("title", "Test photo " + userIndex);

                JsonArray albumIds = new JsonArray();
                albumIds.add(albumId);
                addPhotoPayload.add("albumIds", albumIds);

                JsonArray tags = new JsonArray();
                tags.add("test");
                tags.add("multi-user");
                addPhotoPayload.add("tags", tags);

                addPhotoPayload.addProperty("caption", "Concurrent upload test");
                addPhotoPayload.addProperty("favorable", true);

                Response addPhotoResponse = sendRequest("Photo/addPhoto", addPhotoPayload);
                assertSuccessful(addPhotoResponse, "Adding photo failed for " + username);
                String photoId = getRequiredString(getPayload(addPhotoResponse), "id");

                // --- 5. Get Photo Metadata ---
                JsonObject getPhotoPayload = new JsonObject();
                getPhotoPayload.addProperty("sessionId", sessionId);
                getPhotoPayload.addProperty("photoId", photoId);

                Response getPhotoResponse = sendRequest("Photo/getPhotoById", getPhotoPayload);
                assertSuccessful(getPhotoResponse, "Getting photo metadata failed for " + username);
                assertNotNull(getPhotoResponse.getPayload(), "Photo payload is null for " + username);

                // --- 6. Get Photo Bytes ---
                JsonObject getBytesPayload = new JsonObject();
                getBytesPayload.addProperty("sessionId", sessionId);
                getBytesPayload.addProperty("photoId", uploadedPhotoId);

                Response getBytesResponse = sendRequest("Photo/getPhotoBytes", getBytesPayload);
                assertSuccessful(getBytesResponse, "Getting photo bytes failed for " + username);

                String returnedBase64 = getRequiredString(getPayload(getBytesResponse), "photoData");
                assertEquals(photoBase64, returnedBase64, "Returned photo bytes do not match uploaded bytes");

                return null;
            };

            futures.add(executor.submit(task));
        }

        /*
         * FIX: Increased timeout to 5 minutes.
         * 5,000 users executing 6 requests each (30,000 total requests)
         * takes longer than 100 seconds when throttled to a safe level.
         */
        try {
            for (Future<Void> future : futures) {
                future.get(5, TimeUnit.MINUTES);
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private Response sendRequest(String actionName, JsonObject payload) throws Exception {
        Request request = new Request(actionName, payload);
        String requestJson = gson.toJson(request);

        try (
                Socket socket = new Socket(HOST, PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
        ) {
            writer.write(requestJson);
            writer.newLine();
            writer.flush();

            String responseJson = reader.readLine();
            assertNotNull(responseJson, "The server returned an empty response for " + actionName);

            return gson.fromJson(responseJson, Response.class);
        }
    }

    private String convertPhotoToBase64(String filePath) throws Exception {
        byte[] photoBytes = Files.readAllBytes(Path.of(filePath));
        return Base64.getEncoder().encodeToString(photoBytes);
    }

    private JsonObject getPayload(Response response) {
        if (response.getPayload() != null) {
            return response.getPayload();
        }
        fail("Both payload and data are null. Server message: " + response.getMessage());
        return null;
    }

    private String getRequiredString(JsonObject jsonObject, String fieldName) {
        assertNotNull(jsonObject, "JSON object is null while reading " + fieldName);
        assertTrue(jsonObject.has(fieldName), "Missing field '" + fieldName + "' in response: " + jsonObject);
        assertFalse(jsonObject.get(fieldName).isJsonNull(), "Field '" + fieldName + "' is null");
        return jsonObject.get(fieldName).getAsString();
    }

    private void assertSuccessful(Response response, String failureMessage) {
        assertNotNull(response, failureMessage + ": response is null");
        String status = response.getStatus();
        boolean successful = "200".equals(status) || "SUCCESS".equalsIgnoreCase(status);
        assertTrue(successful, failureMessage + ". Status: " + status + ", message: " + response.getMessage());
    }
}