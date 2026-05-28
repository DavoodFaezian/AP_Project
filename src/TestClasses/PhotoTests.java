import Exceptions.*;
import MainClasses.*;
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
        Exception exp1 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo2.addPhotoToAlbum(null);} );
        assertEquals("The photo is already exists." , exp1.getMessage());
        assertDoesNotThrow(() -> photo1.addPhotoToAlbum(null));
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

    @Test
    public void removePhotoFromAlbumTest(){
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
        Exception exp1 = assertThrows(PhotoDoesNotExistInThisAlbum.class , () -> {photo1.removePhotoFromAlbum(album2);});
        assertEquals( "The photo does not exist in this album.", exp1.getMessage());
        photo1.addPhotoToAlbum(album2);
        assertDoesNotThrow(() -> {photo1.removePhotoFromAlbum(album2);});
        assertFalse(album2.getPhotos().contains(new PhotoAlbum(photo1 , album2)));
        assertEquals(0 , album2.getPhotos().size());
        assertTrue(user1.getPhotos().contains(photo1));
        Exception exp2 = assertThrows(NotOwnersAlbumException.class , () -> {photo4.removePhotoFromAlbum(album1);});
        assertEquals("You can't remove your photo from someone else's album." , exp2.getMessage());
        assertTrue(album1.getPhotos().contains(new PhotoAlbum(photo3 , album1)));
        assertDoesNotThrow(() -> photo3.removePhotoFromAlbum(album1));
        assertFalse(user1.getPhotos().contains(photo3));
        assertDoesNotThrow(() -> photo2.addPhotoToAlbum(album1));
        assertEquals(2 , album1.getPhotos().size());
        assertDoesNotThrow(() -> photo2.removePhotoFromAlbum(album1));
        assertTrue(user1.getPhotos().contains(photo2));
        assertEquals(2 , user1.getPhotos().size());
        Exception exp3 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo2.addPhotoToAlbum(null);});
        assertEquals("Photo is already exists." , exp3.getMessage());
        assertDoesNotThrow(() -> photo2.removePhotoFromAlbum(null));
        assertFalse(user1.getPhotos().contains(photo2));
        Exception exp4 = assertThrows(PhotoDoesNotExistException.class, () -> photo1.removePhotoFromAlbum(null));
        assertEquals("Photo does not exist." , exp4.getMessage());
        photo1.removePhotoFromAlbum(album1);
        assertEquals(0 , user1.getPhotos().size());
    }

}
