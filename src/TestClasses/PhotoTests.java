import DTO.Album.AddAlbumDto;
import DTO.Album.AlbumDto;
import DTO.Album.DeleteAlbumDto;
import DTO.Album.GetAlbumDto;
import DTO.Photo.*;
import DTO.User.LogInDto;
import DTO.User.SignUpDto;
import Exceptions.ActionFailedException;
import MainClasses.User;
import Services.AlbumService;
import Services.PhotoAlbumService;
import Services.PhotoService;
import Services.UserService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PhotoTests {

    @Test
    public void photoTest() {
        UserService userService = UserService.getInstance();
        PhotoService photoService = PhotoService.getInstance();
        AlbumService albumService = AlbumService.getInstance();
        PhotoAlbumService photoAlbumService = PhotoAlbumService.getInstance();
        userService.signUp(new SignUpDto("UniqueName","Unique@1234","Unique@1234"));
        User user1 = userService.getUserMainClass("UniqueName","Unique@1234");
        String sessionId = userService.logIn(new LogInDto("UniqueName","Unique@1234")).getId();
        String albumId1 = albumService.addAlbum(new AddAlbumDto(sessionId,"new album 1")).getId();
        String albumId2 = albumService.addAlbum(new AddAlbumDto(sessionId,"new album 2")).getId();
        String photoId = photoService.addPhoto(new AddPhotoDto(
            sessionId,"new photo","new photo", Set.of(albumId1),new HashSet<>(),"caption",false)).getId();
        photoAlbumService.addPhotoToAlbum(new AddPhotoToAndRemovePhotoFromAlbum(
                sessionId,photoId,albumId2

        ));

        assertEquals(
                1 , PhotoService.getInstance().getPhotosByOwnerId(
                        new GetAllPhotosDto(
                                sessionId
                        )
                ).getPhotos().size()
        );

        PhotoDto photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

        assertEquals(2 , photo.getAlbumIds().size());

        AlbumDto album2 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId2,user1.getId()));

        assertEquals(1 , album2.getPhotoIds().size());

        AlbumDto album1 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId1,user1.getId()));

        assertEquals(1 , album1.getPhotoIds().size());


       photoAlbumService.removePhotoFromAlbum(new AddPhotoToAndRemovePhotoFromAlbum(
               sessionId , photoId , albumId1
       ));

       photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

       album1 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId1,user1.getId()));

       assertEquals(1 , photo.getAlbumIds().size());
       assertEquals(0 , album1.getPhotoIds().size());

       photoAlbumService.movePhoto(new MovePhotoDto(
               sessionId , photoId , albumId2 , albumId1
       ));

       photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

       album1 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId1,user1.getId()));

       album2 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId2,user1.getId()));

       assertEquals(1 , photo.getAlbumIds().size());

       assertEquals(0 , album2.getPhotoIds().size());

       assertEquals(1 , album1.getPhotoIds().size());

       assertThrows(ActionFailedException.class , () -> {
           photoAlbumService.movePhoto(
                   new MovePhotoDto(
                           sessionId , photoId , albumId2 , albumId1

                   )
           );
       });

        assertThrows(ActionFailedException.class , () -> {
            photoAlbumService.movePhoto(
                    new MovePhotoDto(
                            sessionId , photoId , "" , albumId1

                    )
            );
        });

       String photoId2 = photoService.addPhoto(new AddPhotoDto(
          sessionId , "photo2" ,"photo 2",Set.of("") , new HashSet<>() ,
          "caption" , false)).getId();

        assertEquals(
                2 , PhotoService.getInstance().getPhotosByOwnerId(
                        new GetAllPhotosDto(
                                sessionId
                        )
                ).getPhotos().size()
        );

       PhotoDto photo2 = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId2 , user1.getId()));

       assertEquals(1 , photo2.getAlbumIds().size());

       photoAlbumService.addPhotoToAlbum(new AddPhotoToAndRemovePhotoFromAlbum(
               sessionId , photoId , ""
       ));

       photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

       assertEquals(2 , photo.getAlbumIds().size());

       photoAlbumService.movePhoto(new MovePhotoDto(
               sessionId , photoId , "" , albumId2
       ));

       photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

       album2 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId2,user1.getId()));

       assertEquals(1 , album2.getPhotoIds().size());

       albumService.deleteAlbum(new DeleteAlbumDto(
               albumId2 , sessionId
       ));

       photo = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId , user1.getId()));

       assertEquals(1 , photo.getAlbumIds().size());

       photoAlbumService.addPhotoToAlbum(new AddPhotoToAndRemovePhotoFromAlbum(
               sessionId , photoId2 , albumId1
       ));

       photo2 = photoService.getPhotoById(new GetPhotoDto(sessionId,photoId2 , user1.getId()));

       album1 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId1,user1.getId()));

       assertEquals(2 , album1.getPhotoIds().size());

       assertEquals(2 , photo2.getAlbumIds().size());

       photoService.deletePhoto(new DeletePhotoDto(
               sessionId , photoId2
       ));

        assertEquals(
                1 , PhotoService.getInstance().getPhotosByOwnerId(
                        new GetAllPhotosDto(
                                sessionId
                        )
                ).getPhotos().size()
        );

       album1 = albumService.getAlbum(new GetAlbumDto(sessionId,albumId1,user1.getId()));

       assertEquals(1 , album1.getPhotoIds().size());
    }
}