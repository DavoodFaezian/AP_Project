import Exceptions.NotOwnersAlbumException;
import Exceptions.PhotoIsAlreadyExistsInThisAlbumException;
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
        User user3 = new User("Sabrina" , "7498257" , new Account("orwt84") , "oew8rt845");
        Album album1 = new Album(user1 , "album1" , "125");
        Album album2 = new Album(user1 , "album2" , "129");
        Album album3 = new Album(user2 , "album3" , "9346570");
        Photo photo1 = new Photo(user1 , null ,  "photo1" , null , null , false , album1 , false , null , "120");
        Photo photo2 = new Photo(user1 , null ,  "photo2" , null , null , true , null , true , null , "121");
        Photo photo3 = new Photo(user1 , null ,  "photo3" , null , null , false , album1 , true , null , "127");
        Photo photo4 = new Photo(user2 , null ,  "photo4" , null , null , true , album3 , true , null , "1278567");
        Photo photo5 = new Photo(user2 , null ,  "photo5" , null , null , true , album3 , true , null , "1278567");
        assertThrows(NullPointerException.class , () -> {photo1.addPhotoToAlbum(null);} );
        assertThrows(NotOwnersAlbumException.class , () -> {photo2.addPhotoToAlbum(album3);});
        assertThrows(PhotoIsAlreadyExistsInThisAlbumException.class , () -> {photo3.addPhotoToAlbum(album1);});
        Exception exp = assertThrows(NotOwnersAlbumException.class , () -> {photo4.addPhotoToAlbum(album2);});
        assertEquals("You can't add your photo to someone else's album." , exp.getMessage());
        assertDoesNotThrow(() -> {photo1.addPhotoToAlbum(album2);});
        assertDoesNotThrow(() -> {photo2.addPhotoToAlbum(album2);});
        assertEquals(0 , user3.getPhotos().size());
        assertEquals(0 , user3.getAlbums().size());
        assertEquals(2 , user1.getAlbums().size());
        assertEquals(2 , album2.getPhotos().size());
        assertEquals(2 , album3.getPhotos().size());
        assertEquals(3 , user1.getPhotos().size());
        assertEquals(2 , user2.getPhotos().size());
    }

}
