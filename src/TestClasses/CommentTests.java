import DTO.Album.AddAlbumDto;
import DTO.Comment.*;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.AddPhotoToAndRemovePhotoFromAlbum;
import DTO.Post.AddPostDto;
import DTO.Post.EditPostDto;
import DTO.User.LogInDto;
import DTO.User.SignUpDto;
import Exceptions.ActionFailedException;
import Services.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTests {
    static UserService userService = UserService.getInstance();
    static PostService postService = PostService.getInstance();
    CommentService commentService = CommentService.getInstance();
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
    static String postId1;
    static String postId2;
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
        ).getId();

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
        ).getId();

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
        ).getId();
        photoAlbumService.addPhotoToAlbum(
                new AddPhotoToAndRemovePhotoFromAlbum(
                        sessionId,
                        photoId2,
                        albumId1
                )
        );
        postId1 = postService.addPost(
                new AddPostDto(
                        sessionId,
                        Set.of(photoId1,photoId2),
                        Set.of(albumId1,albumId2),
                        true
                )
        ).getId();

        postId2 = postService.addPost(
                new AddPostDto(
                        sessionId,
                        Set.of(photoId2),
                        Set.of(),
                        true
                )
        ).getId();
    }
    @Test
    public void testComments(){
        String commentId1 = commentService.addComment(
                new AddCommentDto(
                        sessionId,
                        "new comment",
                        postId1,
                        userId
                )
        ).getId();
        String commentId2 = commentService.addComment(
                new AddCommentDto(
                        sessionId,
                        "new comment2",
                        postId1,
                        userId
                )
        ).getId();


        String commentId3 = commentService.addComment(
                new AddCommentDto(
                        sessionId,
                        "new comment3",
                        postId2,
                        userId
                )
        ).getId();
        List<String> commentIds = commentService.getAllCommentsByPostId(new GetCommentsByPostDto(
                sessionId,
                postId1
        )).getComments().stream().map(CommentDto::getId).toList();
        assertEquals(List.of(commentId1,commentId2),commentIds);
        commentService.deleteComment(new DeleteCommentDto(
                commentId2,
                sessionId,
                postId1,
                userId
        ));
        commentIds = commentService.getAllCommentsByPostId(new GetCommentsByPostDto(
                sessionId,
                postId1
        )).getComments().stream().map(CommentDto::getId).toList();
        assertFalse(commentIds.contains(commentId2));
        commentService.editComment(
                new EditCommentDto(
                        commentId3,
                        sessionId,
                        postId2,
                        "edited"
                )
        );
        List<CommentDto> commentDtos=commentService.getAllCommentsByPostId(new GetCommentsByPostDto(
                sessionId,
                postId2
        )).getComments();
        assertEquals("edited",commentDtos.getFirst().getScript());
        postService.editPost(
                new EditPostDto(
                        postId1,
                        sessionId,
                        Set.of(photoId1),
                        Set.of(albumId1),
                        false
                )
        );
        assertThrows(ActionFailedException.class,()->commentService.addComment(
                new AddCommentDto(
                        sessionId,
                        "new comment2",
                        postId1,
                        userId
                )
        ));

    }
}
