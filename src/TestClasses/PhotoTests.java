import Exceptions.AccessDeniedException;
import Exceptions.ItemDoesNotExistException;
import MainClasses.*;
import Services.PhotoAlbumService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;


public class PhotoTests{

    private User user1;
    private User comment6;

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
        comment6 = new User("Davood" , "123456789*" , Theme.DARK);

        caption1 = "This is my photo1.";
        caption2 = "This is my photo2.";
        caption4 = "This is my photo4.";
        caption5 = "This is my photo5.";

        String script1 = "I think this place is Margoon Waterfall.";
        String script2 = "I think it will change human's life.";
        String script3 = "I think I'm gonna be hungry.";
        String script4 = "Wow, this city is so crowded.";
        String script5 = "I love abstract art.";
        comment1 = new Comment("2" , script1 , "1");
        comment2 = new Comment("2" , script2 , "1");
        comment3 = new Comment("2" , script3 , "2");
        comment4 = new Comment("1" , script4 , "2");
        comment5 = new Comment("1" , script5 , "3");

        tags1.add("nature");
        tags1.add("beautiful");
        tags2.add("technology");
        tags3.add("food");
        tags4.add("city");
        tags6.add("art");
        tags6.add("abstract");

        album1 = new Album("1" , "album1");
        album2 = new Album("1" , "album2");
        album3 = new Album("1", "album3");
        album4 = new Album("2", "album4");
        album5 = new Album("2", "album5");
        album6 = new Album("2", "album6");


        photo1 = new Photo("1" ,"photo1" , tags1 , caption1 , true , true);
        photo2 = new Photo("1" ,"photo1" , tags2 , caption2 , true , false);
        photo3 = new Photo("1" , "photo3" , tags3 , caption3 , true , true);
        photo4 = new Photo("2","photo4" , tags4 , caption4 , true , true);
        photo5 = new Photo("2", "photo5" , null , caption5 , false , false);
        photo6 = new Photo("2","photo6" , tags6 , null , true , true);

        service = new PhotoAlbumService();

    }

    @Test
    public void addTest(){
        assertEquals(3 , user1.getAlbumIds().size());
        assertEquals(3 , user1.getPhotoIds().size());
        assertEquals(1 , photo1.getPhotoAlbumIds().size());
        assertEquals(1 , photo3.getPhotoAlbumIds().size());
        assertEquals(1 , album2.getPhotoIds().size());
    }
}