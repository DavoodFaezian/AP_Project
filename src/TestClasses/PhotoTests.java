import APIServer.Request;
import DTO.Photo.AddPhotoDto;
import MainClasses.Photo;
import MainClasses.User;
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

        User user1 = UserService.getInstance().getUser("Ali" , "12345678@");

        AddPhotoDto data1 = new AddPhotoDto(user1.getSessionIds().stream().findAny().get() , user1.getId() , "photo1" , null , null , true , true);

        Gson gson = new Gson();

        JsonObject obj = gson.fromJson(gson.toJson(data1) , JsonObject.class);

        Request request1 = new Request("Photo/addPhoto" , obj);

        List<Photo> photos = PhotoRepository.getInstance().getPhotosByOwnerId(user1.getId());



    }
}