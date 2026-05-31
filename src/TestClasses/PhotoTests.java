import Exceptions.*;
import MainClasses.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PhotoTests {

    @Test
    public void addPhotoToAlbumTest(){
        User user1 = new User("Ali" , "123@");
        User user2 = new User("Amir" , "12345&");
        User user3 = new User("Sabrina" , "7498257");
        Album album1 = new Album(user1 , "album1");
        Album album2 = new Album(user1 , "album2");
        Album album3 = new Album(user2 , "album3");
        Photo photo1 = new Photo(user1 , null ,  "photo1" , null , null , false , album1 , false);
        Photo photo2 = new Photo(user1 , null ,  "photo2" , null , null , true , null , true);
        Photo photo3 = new Photo(user1 , null ,  "photo3" , null , null , false , album1 , true);
        Photo photo4 = new Photo(user2 , null ,  "photo4" , null , null , true , album3 , true);
        Photo photo5 = new Photo(user2 , null ,  "photo5" , null , null , true , album3 , true);
        Exception exp1 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo2.addPhotoToAlbum(null);} );
        assertEquals("Photo is already exists!!!" , exp1.getMessage());
        assertDoesNotThrow(() -> photo1.addPhotoToAlbum(null));
        assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo3.addPhotoToAlbum(album1);});
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
        User user1 = new User("Ali" , "123@");
        User user2 = new User("Amir" , "12345&");
        User user3 = new User("Sabrina" , "7498257");
        Album album1 = new Album(user1 , "album1");
        Album album2 = new Album(user1 , "album2");
        Album album3 = new Album(user2 , "album3");
        Photo photo1 = new Photo(user1 , null ,  "photo1" , null , null , false , album1 , false);
        Photo photo2 = new Photo(user1 , null ,  "photo2" , null , null , true , null , true);
        Photo photo3 = new Photo(user1 , null ,  "photo3" , null , null , false , album1 , true);
        Photo photo4 = new Photo(user2 , null ,  "photo4" , null , null , true , album3 , true);
        Photo photo5 = new Photo(user2 , null ,  "photo5" , null , null , true , album3 , true);
        Exception exp1 = assertThrows(PhotoDoesNotExistException.class , () -> {photo1.removePhotoFromAlbum(album2);});
        assertEquals( "Photo does not exist!!!", exp1.getMessage());
        photo1.addPhotoToAlbum(album2);
        assertDoesNotThrow(() -> {photo1.removePhotoFromAlbum(album2);});
        assertFalse(album2.getPhotos().contains(new PhotoAlbum(photo1 , album2)));
        assertEquals(0 , album2.getPhotos().size());
        assertTrue(user1.getPhotos().contains(photo1));
        assertTrue(album1.getPhotos().contains(new PhotoAlbum(photo3 , album1)));
        assertDoesNotThrow(() -> photo3.removePhotoFromAlbum(album1));
        assertFalse(photo3.getAlbums().contains(new PhotoAlbum(photo3 , album1)));
        assertFalse(user1.getPhotos().contains(photo3));
        assertDoesNotThrow(() -> photo2.addPhotoToAlbum(album1));
        assertEquals(2 , album1.getPhotos().size());
        assertDoesNotThrow(() -> photo2.removePhotoFromAlbum(album1));
        assertTrue(user1.getPhotos().contains(photo2));
        assertEquals(2 , user1.getPhotos().size());
        Exception exp3 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo2.addPhotoToAlbum(null);});
        assertEquals("Photo is already exists!!!" , exp3.getMessage());
        assertDoesNotThrow(() -> photo2.removePhotoFromAlbum(null));
        assertFalse(user1.getPhotos().contains(photo2));
        Exception exp4 = assertThrows(PhotoDoesNotExistException.class, () -> photo1.removePhotoFromAlbum(null));
        assertEquals("Photo does not exist!!!" , exp4.getMessage());
        photo1.removePhotoFromAlbum(album1);
        assertEquals(0 , user1.getPhotos().size());
    }

    @Test
    public void transferTest(){
        User user1 = new User("Ali" , "123@");
        User user2 = new User("Amir" , "12345&");
        User user3 = new User("Sabrina" , "7498257");
        Album album1 = new Album(user1 , "album1");
        Album album2 = new Album(user1 , "album2");
        Album album3 = new Album(user2 , "album3");
        Photo photo1 = new Photo(user1 , null ,  "photo1" , null , null , false , album1 , false);
        Photo photo2 = new Photo(user1 , null ,  "photo2" , null , null , true , null , true);
        Photo photo3 = new Photo(user1 , null ,  "photo3" , null , null , false , album1 , true);
        Photo photo4 = new Photo(user2 , null ,  "photo4" , null , null , true , album3 , true);
        Photo photo5 = new Photo(user2 , null ,  "photo5" , null , null , true , album3 , true);
        Exception exp1 = assertThrows(PhotoDoesNotExistException.class , () -> {photo1.transferPhoto(album2 , album1);});
        assertEquals("Photo does not exist in original album!!!" , exp1.getMessage());
        assertEquals(2 , album1.getPhotos().size());
        assertDoesNotThrow(() -> {photo1.transferPhoto(album1 , null);});
        Exception exp2 = assertThrows(PhotoIsAlreadyExistsException.class , () -> {photo1.transferPhoto(null , null);});
        assertEquals("Photo is already exists in destination album!!!" , exp2.getMessage());
        assertEquals(1 , album1.getPhotos().size());
    }


}
