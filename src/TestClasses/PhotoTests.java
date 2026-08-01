import APIServer.Request;
import DTO.Album.AddAlbumDto;
import DTO.Album.DeleteAlbumDto;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.AddPhotoToAndRemovePhotoFromAlbum;
import DTO.Photo.DeletePhotoDto;
import DTO.User.LogInDto;
import DTO.User.SignUpDto;
import Exceptions.ActionFailedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import RequestHandler.RequestHandler;
import Services.AlbumService;
import Services.PhotoAlbumService;
import Services.PhotoService;
import Services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhotoTests {

    @Test
    public void photoTest() {
        UserService userService = UserService.getInstance();
        PhotoService photoService = PhotoService.getInstance();
        AlbumService albumService = AlbumService.getInstance();
        PhotoAlbumService photoAlbumService = PhotoAlbumService.getInstance();
        userService.signUp(new SignUpDto("UniqueName","Unique@1234","Unique@1234"));
        User user1 = userService.getUser("UniqueName","Unique@1234");
        String sessionId = userService.logIn(new LogInDto("UniqueName","Unique@1234"));
        String albumId1 = albumService.addAlbum(new AddAlbumDto(sessionId,"new album 1"));
        String albumId2 = albumService.addAlbum(new AddAlbumDto(sessionId,"new album 2"));
        String photoId = photoService.addPhoto(new AddPhotoDto(
            sessionId,"new photo",albumId1,new HashSet<>(),"caption",false
        ));
        photoAlbumService.addPhotoToAlbum(new AddPhotoToAndRemovePhotoFromAlbum(
                sessionId,photoId,albumId2

        ));

    }
}