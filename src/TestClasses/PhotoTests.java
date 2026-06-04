import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import MainClasses.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;


public class PhotoTests{

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

    private Set<String> tags1 = new HashSet<>();
    private Set<String> tags2 = new HashSet<>();
    private Set<String> tags3 = new HashSet<>();
    private Set<String> tags4 = new HashSet<>();
    private Set<String> tags6 = new HashSet<>();

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
        String script4 = "Wow, this city is so crowded.";
        String script5 = "I love abstract art.";
        comment1 = new Comment(user2 , script1 , "12",photo1);
        comment2 = new Comment(user2 , script2 , "123",photo1);
        comment3 = new Comment(user2 , script3 , "1234",photo2);
        comment4 = new Comment(user1 , script4 , "12345",photo2);
        comment5 = new Comment(user1 , script5 , "123456",photo3);

        tags1.add("nature");
        tags1.add("beautiful");
        tags2.add("technology");
        tags3.add("food");
        tags4.add("city");
        tags6.add("art");
        tags6.add("abstract");

        album1 = new Album(user1 , "album1");
        album2 = new Album(user1 , "album2");
        album3 = new Album(user1 , "album3");
        album4 = new Album(user2 , "album4");
        album5 = new Album(user2 , "album5");
        album6 = new Album(user2 , "album6");


        photo1 = new Photo(user1 , caption1 , "photo1" , tags1 , true , album1 , true);
        photo2 = new Photo(user1 , caption2 , "photo1" , tags2 , true , album2 , false);
        photo3 = new Photo(user1 , null , "photo3" , tags3 , false , null , true);
        photo4 = new Photo(user2 , caption4 , "photo4" , tags4 , false , album4 , true);
        photo5 = new Photo(user2 , caption5 , "photo5" , null , true , album4 , false);
        photo6 = new Photo(user2 , null , "photo6" , tags6 , true , album6 , true);

        service = new PhotoAlbumService();

    }

    @Test
    public void addTest(){
        assertEquals(3 , user1.getAlbums().size());
        assertEquals(3 , user1.getPhotos().size());
        assertEquals(1 , photo1.getAlbums().size());
        assertEquals(1 , photo3.getAlbums().size());
        assertEquals(1 , album2.getPhotos().size());
        Exception exp = assertThrows(AccessDeniedException.class , () -> service.addPhotoToAlbum(photo4 , album1));
        assertEquals("Access Denied!!!" , exp.getMessage());
        assertTrue(album2.getPhotos().contains(photo2));
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo2 , album2));
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo2 , album1));
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo2 , album1));
        assertEquals(2 , photo2.getAlbums().size());
        assertTrue(album1.getPhotos().contains(photo2));
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo1 , null));
        assertTrue(photo1.getAlbums().contains(null));
    }

    @Test
    public void removeTest(){
        Exception exp = assertThrows(AccessDeniedException.class , () -> service.removePhotoFromAlbum(photo1 , album4));
        assertEquals("Access Denied!!!" , exp.getMessage());
        assertDoesNotThrow(() -> service.removePhotoFromAlbum(photo6 , album5));
        assertDoesNotThrow(() -> service.removePhotoFromAlbum(photo5 , null));
        assertTrue(album4.getPhotos().contains(photo4));
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo4 , album4);});
        assertFalse(photo4.getAlbums().contains(album4));
        assertEquals(1 , album4.getPhotos().size());
        assertDoesNotThrow(()-> service.removePhotoFromAlbum(photo4 , album4));
        service.addPhotoToAlbum(photo5 , null);
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo5 , null);});
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo5 , album4);});
        assertFalse(user2.getPhotos().contains(photo5));
    }

    @Test
    public void transferTest(){
        Exception exp1 = assertThrows(ItemDoesNotExistException.class, () -> {service.transferPhoto(photo4 , album5 , album6);});
        assertEquals("Photo wasn't found!!!" , exp1.getMessage());
        Exception exp2 = assertThrows(ItemDoesNotExistException.class, () -> {service.transferPhoto(photo5 , null , album4);});
        assertEquals("Photo wasn't found!!!" , exp2.getMessage());
        Exception exp3 = assertThrows(AccessDeniedException.class , () -> service.transferPhoto(photo1 , album4 , album2));
        assertEquals("Access Denied!!!" , exp3.getMessage());
        assertThrows(AccessDeniedException.class , () -> service.transferPhoto(photo1 , album1 , album5));
        assertThrows(ItemDoesNotExistException.class , () -> {service.transferPhoto(photo4 , null , null);});
        assertThrows(ItemDoesNotExistException.class , () -> {service.transferPhoto(photo4 , album5 , album5);});
        assertFalse(album5.getPhotos().contains(photo6));
        assertFalse(photo6.getAlbums().contains(album5));
        service.addPhotoToAlbum(photo6 , album5);
        assertDoesNotThrow(() -> {service.transferPhoto(photo4 , album4 , album5);});
        assertFalse(album4.getPhotos().contains(photo4));
        assertFalse(photo4.getAlbums().contains(album4));
        assertTrue(album5.getPhotos().contains(photo4));
        assertTrue(photo4.getAlbums().contains(album5));
        service.addPhotoToAlbum(photo6 , null);
        assertDoesNotThrow(() -> {service.transferPhoto(photo6 , null , album4);});
        assertFalse(photo6.getAlbums().contains(null));
        assertTrue(photo6.getAlbums().contains(album4));
        assertTrue(album4.getPhotos().contains(photo6));
        assertDoesNotThrow(() -> {service.transferPhoto(photo5 , album4 , null);});
        assertFalse(photo5.getAlbums().contains(album4));
        assertTrue(photo5.getAlbums().contains(null));
        assertFalse(album4.getPhotos().contains(photo5));
    }

    @Test
    public void shareTest(){
        Exception exp1 = assertThrows(AccessDeniedException.class , () -> service.sharePhoto(photo5 , user1 , user2));
        assertEquals("Access Denied!!!" , exp1.getMessage());
        Exception exp2 = assertThrows(NullPointerException.class , () -> {service.sharePhoto(null , user1 , user2);});
        assertEquals("Photo is null!!!" , exp2.getMessage());
        assertDoesNotThrow(() -> {service.sharePhoto(photo1 , user1 , user2);});
        assertTrue(user2.getSharedPhotos().contains(photo1));
        assertTrue(photo1.getSharedWithUsers().contains(user2));
        assertDoesNotThrow(() -> service.undoSharePhoto(photo1 , user1 , user2));
        assertFalse(photo1.getSharedWithUsers().contains(user2));
        assertFalse(user2.getSharedPhotos().contains(photo1));
    }

    @Test
    public void leaveCommentTest(){
        Exception exp = assertThrows(AccessDeniedException.class , () -> {photo2.addComment(comment3);});
        assertEquals("You can't leave comment for this photo!!!" , exp.getMessage());
        assertDoesNotThrow(() -> {photo1.addComment(comment1);});
        assertTrue(photo1.getComments().contains(comment1));
        assertDoesNotThrow(() -> {photo4.addComment(comment4);});
        assertDoesNotThrow(() -> photo4.addComment(comment5));
        assertEquals(2 , photo4.getComments().size());
        assertThrows(AccessDeniedException.class , () -> {photo2.removeComment(comment4);});
        assertDoesNotThrow(() -> {photo1.removeComment(comment1);});
        assertEquals(0 , photo1.getComments().size());
        assertDoesNotThrow(() -> {photo4.editComment(comment4 , "I changed my mind.");});
        assertEquals("I changed my mind." , comment4.getScript());
    }

}