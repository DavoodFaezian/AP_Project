import MainClasses.Account;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhotoTests {

    @Test
    public void addPhotoToAlbumTest(){
        User user1 = new User("Ali" , "123@" , new Account("126") , "124");
        User user2 = new User("Amir" , "12345&" , new Account("19385") , "9348567");
        Album album1 = new Album(user1 , "album1" , "125");
        Album album2 = new Album(user1 , "album2" , "129");
        Album album3 = new Album(user2 , "album3" , "9346570");
        Photo photo1 = new Photo(user1 , null ,  "photo1" , null , null , false , album1 , false , null , "120");
        Photo photo2 = new Photo(user1 , null ,  "photo2" , null , null , true , null , true , null , "121");
        Photo photo3 = new Photo(user1 , null ,  "photo3" , null , null , false , album1 , true , null , "127");
        assertFalse(user2.getAlbums().isEmpty());
        Photo photo4 = new Photo(user2 , null ,  "photo3" , null , null , true , album3 , true , null , "1278567");
        Photo photo5 = new Photo(user2 , null ,  "photo3" , null , null , true , album1 , true , null , "1278567");
        assertFalse(photo4.addPhotoToAlbum(album3));
        assertFalse(photo3.addPhotoToAlbum(album3));
        assertFalse(photo1.addPhotoToAlbum(album1));
        assertTrue(photo2.addPhotoToAlbum(album1));
        assertTrue(album2.getPhotos().isEmpty());
        assertTrue(photo1.addPhotoToAlbum(album2));
        assertFalse(album2.getPhotos().isEmpty());
        assertFalse(photo1.addPhotoToAlbum(album2));
        assertEquals(1 , photo3.getAlbums().size());
        assertEquals(3 , album1.getPhotos().size());
        assertEquals(2 , user1.getAlbums().size());
    }

}
