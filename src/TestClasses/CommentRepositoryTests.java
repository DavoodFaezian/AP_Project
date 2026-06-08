import Exceptions.AccessDeniedException;
import Exceptions.CommentNotAllowedException;
import Exceptions.FieldIsEmptyException;
import MainClasses.*;
import Repositories.AlbumRepository;
import Repositories.CommentRepository;
import Repositories.PhotoRepository;
import Repositories.UserRepository;
import Services.PhotoAlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommentRepositoryTests {


    private User user1;
    private User user2;

    private String caption1;
    private String caption2;
    private String caption3;
    private String caption4;
    private String caption5;

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;
    private Comment comment4;
    private Comment comment5;
    private Comment comment6;

    private Album album1;
    private Album album2;
    private Album album3;
    private Album album4;
    private Album album5;
    private Album album6;

    private Photo photo1;
    private Photo photo2;
    private Photo photo3;
    private Photo photo4;
    private Photo photo5;
    private Photo photo6;

    private PhotoAlbumService service;
    @BeforeEach
    public void before(){

        user1 = new User("Ali" , "12345678!" , Theme.LIGHT);
        user2 = new User("Davood" , "123456789*" , Theme.DARK);

        caption1 = "This is my photo1.";
        caption2 = "This is my photo2.";
        caption4 = "This is my photo4.";
        caption5 = "This is my photo5.";

        String script1 = "I think this place is Margoon Waterfall.";
        String script2 = "I think it will change human's life.";
        String script3 = "I think I'm gonna be hungry.";
        String script4 = "";
        String script5 = null;
        String script6 = "I love abstract art.";



        album1 = new Album(user1.getId() , "album1" , null);
        album2 = new Album(user1.getId() , "album2" , null);
        album3 = new Album(user1.getId(), "album3" , null);
        album4 = new Album(user2.getId(), "album4" , null);
        album5 = new Album(user2.getId(), "album5" , null);
        album6 = new Album(user2.getId(), "album6" , null);
        HashSet<String> albumIds1 = new HashSet<>();
        albumIds1.add(album1.getId());
        albumIds1.add(album2.getId());
        HashSet<String> albumIds2 = new HashSet<>();
        albumIds1.add(album3.getId());
        albumIds1.add(album4.getId());
        HashSet<String> albumIds3 = new HashSet<>();
        albumIds1.add(album5.getId());
        albumIds1.add(album6.getId());

        photo1 = new Photo(user1.getId(),"photo1" , null , caption1 , true , true , null);
        photo2 = new Photo(user1.getId(),"photo1" , null , caption2 , true , true , null);
        photo3 = new Photo(user1.getId(), "photo3" , null , caption3 , true , true , null);
        photo4 = new Photo(user2.getId(), "photo4" , null , caption4 , true , true , null);
        photo5 = new Photo(user2.getId(), "photo5" , null , caption5 , false , true , null);
        photo6 = new Photo(user2.getId(), "photo6" , null , null , true , false , null);

        comment1 = new Comment(user1.getId(), script1,photo1.getId());
        comment2 = new Comment(user1.getId(), script2,photo2.getId());
        comment3 = new Comment(user2.getId(),script3,photo3.getId());
        comment4 = new Comment(user1.getId(),script4,photo4.getId());
        comment5 = new Comment(user1.getId(),script5,photo5.getId());
        comment6 = new Comment(user2.getId(),script6,photo6.getId());

        service = new PhotoAlbumService();
        UserRepository.getInstance().addUser(user1);
        UserRepository.getInstance().addUser(user2);
        AlbumRepository.getInstance().addAlbum(album1);
        AlbumRepository.getInstance().addAlbum(album2);
        AlbumRepository.getInstance().addAlbum(album3);
        AlbumRepository.getInstance().addAlbum(album4);
        AlbumRepository.getInstance().addAlbum(album5);
        AlbumRepository.getInstance().addAlbum(album6);
        PhotoRepository.getInstance().addPhoto(photo1);
        PhotoRepository.getInstance().addPhoto(photo2);
        PhotoRepository.getInstance().addPhoto(photo3);
        PhotoRepository.getInstance().addPhoto(photo4);
        PhotoRepository.getInstance().addPhoto(photo5);
        PhotoRepository.getInstance().addPhoto(photo6);
        PhotoAlbumService service = new PhotoAlbumService();
        service.addPhotoToAlbum(photo1.getId(),album1.getId());
        service.addPhotoToAlbum(photo1.getId(),album2.getId());
        service.addPhotoToAlbum(photo1.getId(),album3.getId());
        service.addPhotoToAlbum(photo2.getId(),album1.getId());
        service.addPhotoToAlbum(photo2.getId(),album2.getId());
        service.addPhotoToAlbum(photo3.getId(),album1.getId());
        service.addPhotoToAlbum(photo3.getId(),album2.getId());
        service.addPhotoToAlbum(photo4.getId(),album4.getId());
        service.addPhotoToAlbum(photo4.getId(),album5.getId());
        service.addPhotoToAlbum(photo5.getId(),album4.getId());
        service.addPhotoToAlbum(photo5.getId(),album6.getId());
        service.addPhotoToAlbum(photo6.getId(),album5.getId());
    }
    @Test
    public void testAddComment(){
        List<Comment> initialComments = CommentRepository.getInstance().getAllComments();
        CommentRepository.getInstance().addComment(comment1);
        CommentRepository.getInstance().addComment(comment2);
        CommentRepository.getInstance().addComment(comment3);
        initialComments.add(comment1);
        initialComments.add(comment2);
        initialComments.add(comment3);
        assertEquals(initialComments, CommentRepository.getInstance().getAllComments());
        assertThrows(FieldIsEmptyException.class,()->CommentRepository.getInstance().addComment(comment4));
        assertThrows(NullPointerException.class,()->CommentRepository.getInstance().addComment(comment5));
        assertThrows(CommentNotAllowedException.class,()->CommentRepository.getInstance().addComment(comment6));

    }

    @Test
    public void testEditComment(){
        List<Comment> initialComments = CommentRepository.getInstance().getAllComments();
        CommentRepository.getInstance().addComment(comment1);
        CommentRepository.getInstance().addComment(comment2);
        CommentRepository.getInstance().addComment(comment3);
        initialComments.add(comment1);
        initialComments.add(comment2);
        initialComments.add(comment3);
        assertEquals(initialComments, CommentRepository.getInstance().getAllComments());
        comment1.editComment("new comment");

        comment3.editComment(null);
        assertThrows(NullPointerException.class,()->CommentRepository.getInstance().addComment(comment5));
        assertThrows(NullPointerException.class,()->CommentRepository.getInstance().editComment(
                new Comment(user1.getId(),null,photo1.getId())
        ));
        assertThrows(NullPointerException.class,()->CommentRepository.getInstance().editComment(
                new Comment(user1.getId(),"",photo1.getId())
        ));


    }
}
