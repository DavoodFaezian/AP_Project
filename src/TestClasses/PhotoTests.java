import Exceptions.ItemDoesNotExistException;
import Exceptions.PhotoIsAlreadyExistsException;
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

    private PhotoAlbum service;

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
        comment1 = new Comment(user2 , script1 , "12");
        comment2 = new Comment(user2 , script2 , "123");
        comment3 = new Comment(user2 , script3 , "1234");
        comment4 = new Comment(user1 , script4 , "12345");
        comment5 = new Comment(user1 , script5 , "123456");

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

        service = new PhotoAlbum();

    }

    @Test
    public void addTest(){
        assertEquals(3 , user1.getAlbums().size());
        assertEquals(3 , user1.getPhotos().size());
        assertEquals(1 , photo1.getAlbums().size());
        assertEquals(1 , photo3.getAlbums().size());
        assertEquals(1 , album2.getPhotos().size());
        assertTrue(album2.getPhotos().contains(photo2));
        Exception exp1 = assertThrows(PhotoIsAlreadyExistsException.class, () -> {service.addPhotoToAlbum(photo1 , album1);});
        assertEquals("Photo is already exists!!!" , exp1.getMessage());
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo2 , album1));
        assertEquals(2 , photo2.getAlbums().size());
        assertTrue(album1.getPhotos().contains(photo2));
        Exception exp2 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {service.addPhotoToAlbum(photo3 , null);});
        assertEquals("Photo is already exists!!!" , exp2.getMessage());
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo1 , null));
        assertTrue(photo1.getAlbums().contains(null));
    }

    @Test
    public void removeTest(){
        Exception exp1 = assertThrows(ItemDoesNotExistException.class , () -> {service.removePhotoFromAlbum(photo4 , album5);});
        assertEquals("Photo does not exist!!!" , exp1.getMessage());
        assertTrue(album4.getPhotos().contains(photo4));
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo4 , album4);});
        assertFalse(photo4.getAlbums().contains(album4));
        assertEquals(1 , album4.getPhotos().size());
        assertThrows(ItemDoesNotExistException.class,() -> {service.removePhotoFromAlbum(photo5 , null);});
        service.addPhotoToAlbum(photo5 , null);
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo5 , null);});
        assertDoesNotThrow(() -> {service.removePhotoFromAlbum(photo5 , album4);});
        assertFalse(user2.getPhotos().contains(photo5));
    }

    @Test
    public void transferTest(){
        Exception exp1 = assertThrows(ItemDoesNotExistException.class , () -> {service.transferPhoto(photo6 , album4 , album5);});
        assertEquals("Photo does not exist!!!" , exp1.getMessage());
        assertFalse(album5.getPhotos().contains(photo6));
        assertFalse(photo6.getAlbums().contains(album5));
        service.addPhotoToAlbum(photo6 , album5);
        Exception exp2 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {service.transferPhoto(photo6 , album6 , album5);});
        assertEquals("Photo is already exists!!!" , exp2.getMessage());
        assertDoesNotThrow(() -> {service.transferPhoto(photo4 , album4 , album5);});
        assertFalse(album4.getPhotos().contains(photo4));
        assertFalse(photo4.getAlbums().contains(album4));
        assertTrue(album5.getPhotos().contains(photo4));
        assertTrue(photo4.getAlbums().contains(album5));
        assertThrows(PhotoIsAlreadyExistsException.class , () -> {service.transferPhoto(photo6 , album5 ,album5);});
        assertThrows(PhotoIsAlreadyExistsException.class , () -> {service.transferPhoto(photo6 , null , null);});
    }
}