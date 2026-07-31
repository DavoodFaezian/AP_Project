package TestClasses;
import Services.*;
import DTO.Album.AddAlbumDto;
import DTO.Photo.AddPhotoDto;
import DTO.Post.DeletePostDto;
import DTO.Post.EditPostDto;
import DTO.Post.GetPostRelationsDto;
import DTO.Post.GetPostsByOwnerDto;
import DTO.Post.PostDto;
import DTO.User.LogInDto;
import DTO.User.SignUpDto;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import MainClasses.User;
import RequestHandler.RequestHandler;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.PostRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PostServiceTests {

    private final Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        // If your repositories have clear/reset methods, call them here.
        // Example:
        // UserRepository.getInstance().clear();
        // PhotoRepository.getInstance().clear();
        // AlbumRepository.getInstance().clear();
        // PostRepository.getInstance().clear();
        // SessionRepository.getInstance().clear();
    }

    // -----------------------
    // Helpers
    // -----------------------

    private void signUp(String username, String password) {
        SignUpDto dto = new SignUpDto(username, password, password);
        JsonObject json = gson.fromJson(gson.toJson(dto), JsonObject.class);
        RequestHandler handler = new RequestHandler(new APIServer.Request("User/signUp", json));
        assertDoesNotThrow(handler::handle);
    }

    private String logInAndGetSessionId(String username, String password) {
        LogInDto dto = new LogInDto(username, password);
        JsonObject json = gson.fromJson(gson.toJson(dto), JsonObject.class);
        RequestHandler handler = new RequestHandler(new APIServer.Request("User/logIn", json));
        assertDoesNotThrow(handler::handle);

        User user = UserService.getInstance().getUser(username, password);
        assertNotNull(user);
        return user.getSessionIds().stream().findFirst().orElseThrow();
    }

    private User getUserBySession(String sessionId) {
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        assertNotNull(user, "No user found for sessionId=" + sessionId);
        return user;
    }

    private String addPhotoViaService(String sessionId, String photoName) {
        AddPhotoDto dto = new AddPhotoDto(
                sessionId,
                photoName,
                new HashSet<>(Set.of("tag1", "tag2")),
                "caption-" + photoName,
                true,
                true
        );

        assertDoesNotThrow(() -> PhotoService.getInstance().addPhoto(dto));

        User owner = getUserBySession(sessionId);
        List<Photo> photos = PhotoRepository.getInstance().getPhotosByOwnerId(owner.getId());
        assertNotNull(photos);

        Photo created = photos.stream()
                .filter(p -> photoName.equals(p.getPhotoName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Photo not found: " + photoName));

        return created.getId();
    }

    private String addAlbumViaService(String sessionId, String albumName) {
        AddAlbumDto dto = new AddAlbumDto(sessionId, albumName);
        assertDoesNotThrow(() -> AlbumService.getInstance().addAlbum(dto));

        User owner = getUserBySession(sessionId);
        List<Album> albums = AlbumRepository.getInstance().getAlbumsByOwner(owner.getId());
        assertNotNull(albums);

        Album created = albums.stream()
                .filter(a -> albumName.equals(a.getAlbumName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Album not found: " + albumName));

        return created.getId();
    }

    private String addPostViaService(String sessionId, String ownerId, Set<String> photoIds, Set<String> albumIds, boolean commentsAllowed) {
        PostDto dto = new PostDto(
                sessionId,
                ownerId,
                photoIds,
                albumIds,
                commentsAllowed
        );

        assertDoesNotThrow(() -> PostService.getInstance().addPost(dto));

        List<Post> posts = PostRepository.getInstance().getPostsByOwnerId(ownerId);
        assertNotNull(posts);
        assertFalse(posts.isEmpty(), "No post created for ownerId=" + ownerId);

        Post created = posts.stream()
                .filter(p ->
                        Objects.equals(p.getPhotoIds(), photoIds) &&
                                Objects.equals(p.getAlbumIds(), albumIds) &&
                                Objects.equals(p.getCommentsAllowed(), commentsAllowed)
                )
                .reduce((first, second) -> second)
                .orElse(posts.get(posts.size() - 1));

        return created.getId();
    }

    // -----------------------
    // Tests
    // -----------------------

    @Test
    void addPost_shouldCreateBidirectionalLinks_withMultiplePhotosAndAlbums() {
        signUp("owner1", "Pass1234@");
        signUp("user2", "Pass1234@");

        String ownerSession = logInAndGetSessionId("owner1", "Pass1234@");
        String ownerId = getUserBySession(ownerSession).getId();

        String photo1 = addPhotoViaService(ownerSession, "photo1");
        String photo2 = addPhotoViaService(ownerSession, "photo2");
        String album1 = addAlbumViaService(ownerSession, "album1");
        String album2 = addAlbumViaService(ownerSession, "album2");

        String postId = addPostViaService(
                ownerSession,
                ownerId,
                new HashSet<>(Set.of(photo1, photo2)),
                new HashSet<>(Set.of(album1, album2)),
                true
        );

        Post post = PostRepository.getInstance().findPostById(postId, ownerId);
        assertNotNull(post);
        assertEquals(new HashSet<>(Set.of(photo1, photo2)), post.getPhotoIds());
        assertEquals(new HashSet<>(Set.of(album1, album2)), post.getAlbumIds());
        assertTrue(post.getCommentsAllowed());

        Photo p1 = PhotoRepository.getInstance().findPhotoById(photo1, ownerId);
        Photo p2 = PhotoRepository.getInstance().findPhotoById(photo2, ownerId);
        assertTrue(p1.getPostIds().contains(postId));
        assertTrue(p2.getPostIds().contains(postId));

        Album a1 = AlbumRepository.getInstance().findAlbumById(album1, ownerId);
        Album a2 = AlbumRepository.getInstance().findAlbumById(album2, ownerId);
        assertTrue(a1.getPostIds().contains(postId));
        assertTrue(a2.getPostIds().contains(postId));
    }

    @Test
    void editPost_shouldUpdatePhotosAndAlbumsWithDiffLogic() {
        signUp("owner2", "Pass1234@");

        String ownerSession = logInAndGetSessionId("owner2", "Pass1234@");
        String ownerId = getUserBySession(ownerSession).getId();

        String photo1 = addPhotoViaService(ownerSession, "p1");
        String photo2 = addPhotoViaService(ownerSession, "p2");
        String photo3 = addPhotoViaService(ownerSession, "p3");

        String album1 = addAlbumViaService(ownerSession, "a1");
        String album2 = addAlbumViaService(ownerSession, "a2");

        String postId = addPostViaService(
                ownerSession,
                ownerId,
                new HashSet<>(Set.of(photo1, photo2)),
                new HashSet<>(Set.of(album1)),
                true
        );

        EditPostDto editDto = new EditPostDto(
                postId,
                ownerSession,
                ownerId,
                new HashSet<>(Set.of(photo2, photo3)),
                new HashSet<>(Set.of(album2)),
                false
        );

        assertDoesNotThrow(() -> PostService.getInstance().editPost(editDto));

        Post edited = PostRepository.getInstance().findPostById(postId, ownerId);
        assertNotNull(edited);
        assertEquals(new HashSet<>(Set.of(photo2, photo3)), edited.getPhotoIds());
        assertEquals(new HashSet<>(Set.of(album2)), edited.getAlbumIds());
        assertFalse(edited.getCommentsAllowed());

        Photo oldPhoto = PhotoRepository.getInstance().findPhotoById(photo1, ownerId);
        Photo keptPhoto = PhotoRepository.getInstance().findPhotoById(photo2, ownerId);
        Photo newPhoto = PhotoRepository.getInstance().findPhotoById(photo3, ownerId);

        assertFalse(oldPhoto.getPostIds().contains(postId));
        assertTrue(keptPhoto.getPostIds().contains(postId));
        assertTrue(newPhoto.getPostIds().contains(postId));

        Album oldAlbum = AlbumRepository.getInstance().findAlbumById(album1, ownerId);
        Album newAlbum = AlbumRepository.getInstance().findAlbumById(album2, ownerId);

        assertFalse(oldAlbum.getPostIds().contains(postId));
        assertTrue(newAlbum.getPostIds().contains(postId));
    }

    @Test
    void editPost_withSameSetsShouldNotBreakRelations() {
        signUp("owner3", "Pass1234@");

        String sessionId = logInAndGetSessionId("owner3", "Pass1234@");
        String ownerId = getUserBySession(sessionId).getId();

        String photo1 = addPhotoViaService(sessionId, "same-photo");
        String album1 = addAlbumViaService(sessionId, "same-album");

        String postId = addPostViaService(
                sessionId,
                ownerId,
                new HashSet<>(Set.of(photo1)),
                new HashSet<>(Set.of(album1)),
                true
        );

        EditPostDto editDto = new EditPostDto(
                postId,
                sessionId,
                ownerId,
                new HashSet<>(Set.of(photo1)),
                new HashSet<>(Set.of(album1)),
                true
        );

        assertDoesNotThrow(() -> PostService.getInstance().editPost(editDto));

        Post post = PostRepository.getInstance().findPostById(postId, ownerId);
        assertNotNull(post);
        assertEquals(Set.of(photo1), post.getPhotoIds());
        assertEquals(Set.of(album1), post.getAlbumIds());

        Photo photo = PhotoRepository.getInstance().findPhotoById(photo1, ownerId);
        assertTrue(photo.getPostIds().contains(postId));

        Album album = AlbumRepository.getInstance().findAlbumById(album1, ownerId);
        assertTrue(album.getPostIds().contains(postId));
    }

    @Test
    void deletePost_shouldRemoveBackReferencesFromPhotosAndAlbums() {
        signUp("owner4", "Pass1234@");
        signUp("user4", "Pass1234@");

        String sessionId = logInAndGetSessionId("owner4", "Pass1234@");
        String ownerId = getUserBySession(sessionId).getId();

        String photo1 = addPhotoViaService(sessionId, "del-photo1");
        String photo2 = addPhotoViaService(sessionId, "del-photo2");
        String album1 = addAlbumViaService(sessionId, "del-album1");

        String postId = addPostViaService(
                sessionId,
                ownerId,
                new HashSet<>(Set.of(photo1, photo2)),
                new HashSet<>(Set.of(album1)),
                true
        );

        DeletePostDto deleteDto = new DeletePostDto(sessionId, postId);
        assertDoesNotThrow(() -> PostService.getInstance().deletePost(deleteDto));

        assertThrows(Exception.class, () -> PostRepository.getInstance().findPostById(postId, ownerId));

        Photo p1 = PhotoRepository.getInstance().findPhotoById(photo1, ownerId);
        Photo p2 = PhotoRepository.getInstance().findPhotoById(photo2, ownerId);
        Album a1 = AlbumRepository.getInstance().findAlbumById(album1, ownerId);

        assertFalse(p1.getPostIds().contains(postId));
        assertFalse(p2.getPostIds().contains(postId));
        assertFalse(a1.getPostIds().contains(postId));
    }

    @Test
    void getPostRelations_shouldReturnEmptySetsForEmptyPost_andActualIdsForPopulatedPost() {
        signUp("owner5", "Pass1234@");

        String sessionId = logInAndGetSessionId("owner5", "Pass1234@");
        String ownerId = getUserBySession(sessionId).getId();

        String emptyPostId = addPostViaService(
                sessionId,
                ownerId,
                new HashSet<>(),
                new HashSet<>(),
                true
        );

        Set<String> emptyPhotos = PostService.getInstance().getPhotoIdsOfPost(new GetPostRelationsDto(sessionId, emptyPostId));
        Set<String> emptyAlbums = PostService.getInstance().getAlbumIdsOfPost(new GetPostRelationsDto(sessionId, emptyPostId));

        assertNotNull(emptyPhotos);
        assertNotNull(emptyAlbums);
        assertTrue(emptyPhotos.isEmpty());
        assertTrue(emptyAlbums.isEmpty());

        String photoId = addPhotoViaService(sessionId, "rel-photo");
        String albumId = addAlbumViaService(sessionId, "rel-album");

        String postId = addPostViaService(
                sessionId,
                ownerId,
                new HashSet<>(Set.of(photoId)),
                new HashSet<>(Set.of(albumId)),
                true
        );

        Set<String> photos = PostService.getInstance().getPhotoIdsOfPost(new GetPostRelationsDto(sessionId, postId));
        Set<String> albums = PostService.getInstance().getAlbumIdsOfPost(new GetPostRelationsDto(sessionId, postId));

        assertEquals(Set.of(photoId), photos);
        assertEquals(Set.of(albumId), albums);
    }

    @Test
    void otherUser_shouldNotEditOrDeleteYourPost() {
        signUp("owner6", "Pass1234@");
        signUp("attacker6", "Pass1234@");

        String ownerSession = logInAndGetSessionId("owner6", "Pass1234@");
        String attackerSession = logInAndGetSessionId("attacker6", "Pass1234@");

        String ownerId = getUserBySession(ownerSession).getId();
        String attackerId = getUserBySession(attackerSession).getId();

        String photoId = addPhotoViaService(ownerSession, "secure-photo");
        String postId = addPostViaService(
                ownerSession,
                ownerId,
                new HashSet<>(Set.of(photoId)),
                new HashSet<>(),
                true
        );

        EditPostDto attackerEdit = new EditPostDto(
                postId,
                attackerSession,
                attackerId,
                new HashSet<>(Set.of(photoId)),
                new HashSet<>(),
                false
        );

        assertThrows(Exception.class, () -> PostService.getInstance().editPost(attackerEdit));

        DeletePostDto attackerDelete = new DeletePostDto(attackerSession, postId);
        assertThrows(Exception.class, () -> PostService.getInstance().deletePost(attackerDelete));

        Post stillExists = PostRepository.getInstance().findPostById(postId, ownerId);
        assertNotNull(stillExists);
    }

    @Test
    void invalidSession_shouldThrowIllegalStateException() {
        PostDto dto = new PostDto(
                "invalid-session",
                "fake-owner",
                new HashSet<>(),
                new HashSet<>(),
                true
        );

        Exception ex = assertThrows(IllegalStateException.class, () -> PostService.getInstance().addPost(dto));
        assertEquals("User is not logged in.", ex.getMessage());

        Exception ex2 = assertThrows(IllegalStateException.class,
                () -> PostService.getInstance().getAllPostsByOwnerId(new GetPostsByOwnerDto("invalid-session")));
        assertEquals("User is not logged in.", ex2.getMessage());
    }
}
