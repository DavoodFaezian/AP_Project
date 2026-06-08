import Exceptions.AccessDeniedException;
import Exceptions.CommentNotAllowedException;
import Exceptions.FieldIsEmptyException;
import MainClasses.*;
import Repositories.AlbumRepository;
import Repositories.CommentRepository;
import Repositories.PhotoRepository;
import Repositories.UserRepository;
import Services.CommentService;
import Services.PhotoAlbumService;
import ViewModels.Comment.CommentViewModel;
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



        album1 = new Album(user1.getId() , "album1");
        album2 = new Album(user1.getId() , "album2");
        album3 = new Album(user1.getId(), "album3");
        album4 = new Album(user2.getId(), "album4");
        album5 = new Album(user2.getId(), "album5");
        album6 = new Album(user2.getId(), "album6");
        HashSet<String> albumIds1 = new HashSet<>();
        albumIds1.add(album1.getId());
        albumIds1.add(album2.getId());
        HashSet<String> albumIds2 = new HashSet<>();
        albumIds1.add(album3.getId());
        albumIds1.add(album4.getId());
        HashSet<String> albumIds3 = new HashSet<>();
        albumIds1.add(album5.getId());
        albumIds1.add(album6.getId());

        photo1 = new Photo(user1.getId(),"photo1" , null , caption1 , true , true);
        photo2 = new Photo(user1.getId(),"photo1" , null , caption2 , true , true);
        photo3 = new Photo(user1.getId(), "photo3" , null , caption3 , true , true);
        photo4 = new Photo(user2.getId(), "photo4" , null , caption4 , true , true);
        photo5 = new Photo(user2.getId(), "photo5" , null , caption5 , false , true);
        photo6 = new Photo(user2.getId(), "photo6" , null , null , true , false);

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
        CommentService.editComment(new CommentViewModel(comment2.getId(),"New comment"));
        CommentService.editComment(new CommentViewModel(comment3.getId(),"Edited"));
        assertEquals("New comment", comment2.getScript());
        assertEquals("Edited", comment3.getScript());
        assertThrows(NullPointerException.class,()->CommentService.editComment(new CommentViewModel(comment1.getId(),null)));
        assertThrows(FieldIsEmptyException.class,()->CommentService.editComment(new CommentViewModel(comment1.getId(),"")));


    }
}
