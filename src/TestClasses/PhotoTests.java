import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import Exceptions.ItemNotFoundException;
import MainClasses.*;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.UserRepository;
import Services.PhotoAlbumService;

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
        user1 = new User("Ali" , "12345678!");
        user2 = new User("Davood" , "123456789*");

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

    }

    @Test
    public void addTest(){
        Exception exp = assertThrows(AccessDeniedException.class , () -> service.addPhotoToAlbum(photo1.getId(), album4.getId()));
        assertEquals("Access Denied!!!" , exp.getMessage());
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo1.getId() , album2.getId()));
        assertTrue(photo1.getPhotoAlbumIds().contains(album2.getId()));
        assertTrue(album2.getPhotoIds().contains(photo1.getId()));
        assertDoesNotThrow(() -> service.addPhotoToAlbum(photo3.getId() , ""));
        assertTrue(photo3.getPhotoAlbumIds().contains(""));
    }

    @Test
    public void removeTest(){
        Exception exp = assertThrows(AccessDeniedException.class , () -> service.removePhotoFromAlbum(photo4.getId() , album2.getId()));
        assertEquals("Access Denied!!!" , exp.getMessage());
        assertFalse(photo5.getPhotoAlbumIds().contains(album6.getId()));
        assertFalse(album6.getPhotoIds().contains(photo5.getId()));
        assertDoesNotThrow(() -> service.removePhotoFromAlbum(photo5.getId() , album6.getId()));
        service.addPhotoToAlbum(photo5.getId(), album6.getId());
        assertDoesNotThrow(() -> service.removePhotoFromAlbum(photo5.getId() , album6.getId()));
        assertFalse(photo5.getPhotoAlbumIds().contains(album6.getId()));
        assertFalse(album6.getPhotoIds().contains(photo5.getId()));
        service.addPhotoToAlbum(photo4.getId() , "");
        assertDoesNotThrow(() -> service.removePhotoFromAlbum(photo4.getId() , ""));
        assertFalse(photo4.getPhotoAlbumIds().contains(""));
    }

    @Test
    public void transferTest(){
        Exception exp1 = assertThrows(AccessDeniedException.class , () -> service.transferPhoto(photo3.getId() , album4.getId() , album1.getId()));
        assertEquals("Access Denied!!!" , exp1.getMessage());
        Exception exp2 = assertThrows(AccessDeniedException.class , () -> service.transferPhoto(photo3.getId() , album2.getId() , album5.getId()));
        assertEquals("Access Denied!!!" , exp2.getMessage());
        assertThrows(ItemNotFoundException.class , () -> service.transferPhoto(photo1.getId() , album2.getId() , album3.getId()));
        assertThrows(ItemNotFoundException.class , () -> service.transferPhoto(photo2.getId() , "" , album1.getId()));
        assertThrows(ItemNotFoundException.class , () -> service.transferPhoto(photo3.getId() , album1.getId() , ""));
        assertThrows(ItemNotFoundException.class , () -> service.transferPhoto(photo5.getId() , "" , ""));
        service.addPhotoToAlbum(photo1.getId() , album2.getId());
        assertDoesNotThrow(() -> service.transferPhoto(photo1.getId() , album2.getId() , album3.getId()));
        assertFalse(album2.getPhotoIds().contains(photo1.getId()));
        assertTrue(album3.getPhotoIds().contains(photo1.getId()));
        assertFalse(photo1.getPhotoAlbumIds().contains(album2.getId()));
        assertTrue(photo1.getPhotoAlbumIds().contains(album3.getId()));
        assertDoesNotThrow(() -> service.transferPhoto(photo1.getId() , album3.getId() , ""));
        assertFalse(album3.getPhotoIds().contains(photo1.getId()));
        assertFalse(photo1.getPhotoAlbumIds().contains(album3.getId()));
        assertTrue(photo1.getPhotoAlbumIds().contains(""));
        assertDoesNotThrow(() -> service.transferPhoto(photo1.getId() , "" , album1.getId()));
        assertTrue(album1.getPhotoIds().contains(photo1.getId()));
        assertFalse(photo1.getPhotoAlbumIds().contains(""));
        assertTrue(photo1.getPhotoAlbumIds().contains(album1.getId()));
    }
}