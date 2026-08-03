import DTO.Album.AddAlbumDto;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.AddPhotoToAndRemovePhotoFromAlbum;
import DTO.Post.*;
import DTO.User.FollowAndUnfollowDto;
import DTO.User.LogInDto;
import DTO.User.SignUpDto;
import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import MainClasses.Post;
import Services.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostTests {
    static UserService userService = UserService.getInstance();
    PostService postService = PostService.getInstance();
    static PhotoService photoService = PhotoService.getInstance();
    static AlbumService albumService = AlbumService.getInstance();
    static PhotoAlbumService photoAlbumService = PhotoAlbumService.getInstance();
    static String sessionId;

    static String userId;
    static String albumId1;

    static String albumId2;
    static String photoId1;

    static String photoId2;

    static String photoId3;
    @BeforeAll
    public static void startup(){
        userService.signUp(
                new SignUpDto(
                        "UniName",
                        "newPass@123",
                        "newPass@123"
                )
        );
        sessionId = userService.logIn(new LogInDto(
                "UniName",
                "newPass@123"
        )).getId();

        userId = userService.getUserMainClass(
                "UniName",
                "newPass@123"
        ).getId();
        albumId1 = albumService.addAlbum(
                new AddAlbumDto(
                        sessionId,
                        "new album"
                )
        ).getId();

        albumId2 = albumService.addAlbum(
                new AddAlbumDto(
                        sessionId,
                        "new album 2"
                )
        ).getId();
        photoId1 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId,
                        "new photo",
                        "new photo",
                        albumId1,
                        new HashSet<>(),
                        "caption",
                        false

                )
        );

        photoId2 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId,
                        "new photo 2",
                        "new photo 2",
                        albumId1,
                        new HashSet<>(),
                        "caption 2",
                        false

                )
        );

        photoId3 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId,
                        "new photo 3",
                        "new photo 3",
                        albumId2,
                        new HashSet<>(),
                        "caption 3",
                        false

                )
        );
        photoAlbumService.addPhotoToAlbum(
                new AddPhotoToAndRemovePhotoFromAlbum(
                        sessionId,
                        photoId2,
                        albumId1
                )
        );
    }
    @Test
    public void testPosts(){

        String post1 = postService.addPost(
                new AddPostDto(
                        sessionId,
                        Set.of(photoId1,photoId2),
                        Set.of(albumId1,albumId2),
                        true
                )
        );

        String post2 = postService.addPost(
                new AddPostDto(
                        sessionId,
                        Set.of(photoId2),
                        Set.of(),
                        true
                )
        );
        List<PostDto> postDtos1 = postService.getAllPostsByOwnerId(new GetPostsByOwnerDto(
                sessionId
        ));
        List<PostDto> expectedPostDtos1 = List.of(
                new PostDto(
                        new Post(
                                userId,
                                Set.of(photoId1,photoId2),
                                Set.of(albumId1,albumId2),
                                true
                        )
                ),

                new PostDto(
                        new Post(
                                userId,
                                Set.of(photoId2),
                                Set.of(),
                                true
                        )
                )
        );

        for (int i = 0; i < postDtos1.size(); i++) {
            assertEquals(expectedPostDtos1.get(i),postDtos1.get(i));
        }


        Set<String> albumIds = postService.getAlbumIdsOfPost(
            new GetPostRelationsDto(
                    sessionId,
                    post1
            )
        );
        assertEquals(Set.of(albumId1,albumId2),albumIds);


        Set<String> photoIds = postService.getPhotoIdsOfPost(
                new GetPostRelationsDto(
                        sessionId,
                        post2
                )
        );
        assertEquals(Set.of(photoId2),photoIds);
        postService.editPost(
                new EditPostDto(
                        post1,
                        sessionId,
                        Set.of(photoId2),
                        Set.of(),
                        true
                )
        );
        Set<String> photoIds2 = postService.getAlbumIdsOfPost(
                new GetPostRelationsDto(
                        sessionId,
                        post1
                )
        );
        assertEquals(Set.of(),photoIds2);
        postService.editPost(
                new EditPostDto(
                        post2,
                        sessionId,
                        Set.of(photoId3),
                        Set.of(),
                        true
                )
        );
        List<PostDto> postDtos = postService.getAllPostsByOwnerId(new GetPostsByOwnerDto(
                sessionId
        ));
        List<PostDto> expectedPostDtos = List.of(
                new PostDto(
                        new Post(
                                userId,
                                Set.of(photoId2),
                                Set.of(),
                                true
                        )
                ),

                new PostDto(
                        new Post(
                                userId,
                                Set.of(photoId3),
                                Set.of(),
                                true
                                )
                )
        );
        for (int i = 0; i < postDtos.size(); i++) {
            assertEquals(expectedPostDtos.get(i),postDtos.get(i));
        }
        assertThrows(ActionFailedException.class,()->postService.addPost(
                new AddPostDto(
                        sessionId,
                        Set.of(),
                        Set.of(),
                        true
                )
        ));

        assertThrows(ActionFailedException.class,()->postService.editPost(
                new EditPostDto(
                        post2,
                        sessionId,
                        Set.of(),
                        Set.of(),
                        true
                )
        ));
        postService.deletePost(
                new DeletePostDto(
                        sessionId,
                        post1
                )
        );
        assertThrows(ItemNotFoundException.class,()->postService.getPostById(post1,userId));
        assertEquals(Set.of(photoId3),postService.getPostById(post2,userId).getPhotoIds());


    }
    @Test
    public void testGetPostsByFollowings(){
        userService.signUp(
                new SignUpDto(
                        "Uni2Name",
                        "newPass2@123",
                        "newPass2@123"
                )
        );
        String sessionId2 = userService.logIn(new LogInDto(
                "Uni2Name",
                "newPass2@123"
        )).getId();

        String followingUserId = userService.getUserMainClass(
                "Uni2Name",
                "newPass2@123"
        ).getId();

        userService.signUp(
                new SignUpDto(
                        "Uni3Name",
                        "newPass3@123",
                        "newPass3@123"
                )
        );
        String sessionId3 = userService.logIn(new LogInDto(
                "Uni3Name",
                "newPass3@123"
        )).getId();

        String followingUserId2 = userService.getUserMainClass(
                "Uni3Name",
                "newPass3@123"
        ).getId();

        String albumId4 = albumService.addAlbum(
                new AddAlbumDto(
                        sessionId2,
                        "new album"
                )
        ).getId();

        String albumId5 = albumService.addAlbum(
                new AddAlbumDto(
                        sessionId2,
                        "new album 2"
                )
        ).getId();
        String photoId3 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId2,
                        "new photo 1 for following user",
                        "new photo 1 for following user",
                        albumId5,
                        new HashSet<>(),
                        "caption",
                        false

                )
        );

        String photoId4 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId2,
                        "new photo 2 for following user",
                        "new photo 2 for following user",
                        albumId5,
                        new HashSet<>(),
                        "caption 2",
                        false

                )
        );

        String photoId5 = photoService.addPhoto(
                new AddPhotoDto(
                        sessionId2,
                        "new photo 3 for following user",
                        "new photo 3 for following user",
                        albumId4,
                        new HashSet<>(),
                        "caption 3",
                        false

                )
        );
        photoAlbumService.addPhotoToAlbum(
                new AddPhotoToAndRemovePhotoFromAlbum(
                        sessionId2,
                        photoId3,
                        albumId4
                )
        );
        String post1 = postService.addPost(
                new AddPostDto(
                        sessionId2,
                        Set.of(photoId4),
                        Set.of(albumId4),
                        true
                )
        );

        String post2 = postService.addPost(
                new AddPostDto(
                        sessionId2,
                        Set.of(photoId5),
                        Set.of(albumId4),
                        true
                )
        );


        String albumId6 = albumService.addAlbum(
                new AddAlbumDto(
                        sessionId3,
                        "new album for following user 2"
                )
        ).getId();
        String post3 = postService.addPost(
                new AddPostDto(
                        sessionId3,
                        Set.of(),
                        Set.of(albumId6),
                        true
                )
        );
        userService.follow(
                new FollowAndUnfollowDto(
                     sessionId, followingUserId
                )
        );

        userService.follow(
                new FollowAndUnfollowDto(
                        sessionId, followingUserId2
                )
        );


        TreeSet<PostDto> postDtos = new TreeSet<>(Comparator.comparing(PostDto::getLastModified));
        postDtos.addAll(postService.getAllPostsByOwnerId(new GetPostsByOwnerDto(
                sessionId2
        )));
        postDtos.addAll(postService.getAllPostsByOwnerId(new GetPostsByOwnerDto(
                sessionId3
        )));

        assertEquals(postDtos,postService.getAllPostsOfFollowings(
                userId
        ));
    }
}
