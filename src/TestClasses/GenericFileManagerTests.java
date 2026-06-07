import FileManager.GenericFileManager;
import MainClasses.*;
import Services.PhotoAlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;




public class GenericFileManagerTests{

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
        comment1 = new Comment(user2 , script1 , photo1);
        comment2 = new Comment(user2 , script2 , photo1);
        comment3 = new Comment(user2 , script3 , photo2);
        comment4 = new Comment(user1 , script4 , photo3);
        comment5 = new Comment(user1 , script5 , photo4);

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
    public void writeTest(){
        GenericFileManager<Photo> genericFileManager = new GenericFileManager<>("photo.txt");
        genericFileManager.addToList(photo1);
        genericFileManager.addToList(photo2);
        genericFileManager.addToList(photo3);
        genericFileManager.addToList(photo4);
        genericFileManager.save();
        GenericFileManager<Photo> genericFileManager2 = new GenericFileManager<>("photo.txt");
        List<Photo> photos = genericFileManager2.getAll();
        assertEquals(new ArrayList<Photo>(Arrays.asList(photo1,photo2,photo3,photo4)),photos);

    }
}