import APIServer.Request;
import DTO.Album.AddAlbumDto;
import DTO.Album.DeleteAlbumDto;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.DeletePhotoDto;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import RequestHandler.RequestHandler;
import Services.PhotoService;
import Services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PhotoTests {

    @Test
    public void photoTest() {

        User user1 = UserService.getInstance().getUser("Ali" , "99999999@");

        AddPhotoDto data1 = new AddPhotoDto(user1.getSessionIds().stream().findAny().get() , "photo1" , null , null , true , true);

        Gson gson = new Gson();

        JsonObject obj = gson.fromJson(gson.toJson(data1) , JsonObject.class);

        Request request1 = new Request("Photo/addPhoto" , obj);

        RequestHandler handler1 = new RequestHandler(request1);
        assertDoesNotThrow(handler1::handle);

        assertEquals(1 , user1.getPhotoIds().size());

        Photo photo1 = PhotoRepository.getInstance().findPhotoById(user1.getPhotoIds().stream().findFirst().get() , user1.getId());

        DeletePhotoDto data2 = new DeletePhotoDto(user1.getSessionIds().stream().findFirst().get() , photo1.getId());

        obj = gson.fromJson(gson.toJson(data2) , JsonObject.class);

        Request request2 = new Request("Photo/deletePhoto" , obj);
        RequestHandler handler2 = new RequestHandler(request2);

        assertDoesNotThrow(handler2::handle);

        List<Album> albums = AlbumRepository.getInstance().getAlbumsByOwner(user1.getId());

        Album album = AlbumRepository.getInstance().findAlbumById(albums.get(0).getId() , user1.getId());

        DeleteAlbumDto data4 = new DeleteAlbumDto(album.getId() , user1.getSessionIds().stream().findFirst().get());

        obj = gson.fromJson(gson.toJson(data4) , JsonObject.class);

        Request request5 = new Request("Album/deleteAlbum" , obj);

        RequestHandler handler5 = new RequestHandler(request5);

        assertDoesNotThrow(handler5::handle);

        assertEquals(12 , user1.getAlbumIds().size());

    }
}