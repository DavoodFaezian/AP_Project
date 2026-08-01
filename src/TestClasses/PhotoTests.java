import APIServer.Request;
import DTO.Album.AddAlbumDto;
import DTO.Album.DeleteAlbumDto;
import DTO.Photo.AddPhotoDto;
import DTO.Photo.AddPhotoToAndRemovePhotoFromAlbum;
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

        User user1 = UserService.getInstance().getUser("Hamid" , "12345678@");

        List<Photo> photos = PhotoRepository.getInstance().getPhotosByOwnerId(user1.getId());
        List<Album> albums = AlbumRepository.getInstance().getAlbumsByOwner(user1.getId());

        Gson gson = new Gson();

        AddPhotoToAndRemovePhotoFromAlbum data1 = new AddPhotoToAndRemovePhotoFromAlbum(user1.getSessionIds().stream().findFirst().get(), photos.getFirst().getId() , albums.getFirst().getId() );
        AddPhotoDto data2 = new AddPhotoDto(user1.getSessionIds().stream().findFirst().get(), "photo3" , null , null , null , true);

        JsonObject obj = gson.fromJson(gson.toJson(data2) , JsonObject.class);

        Request request1 = new Request("Photo/addPhoto" , obj);

        RequestHandler handler = new RequestHandler(request1);

        Album album = albums.getFirst();

        assertEquals(1 , album.getPhotoIds().size());

    }
}