import APIServer.Request;
import Dto.*;
import Exceptions.ActionFailedException;
import MainClasses.User;
import RequestHandler.RequestHandler;
import Services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    public void signUpTest() {

        Gson gson = new Gson();

        SignUpDto data1 = new SignUpDto("Ali" , "12345678@" , "12345678@");

        JsonObject obj = gson.fromJson(gson.toJson(data1) , JsonObject.class);

        Request request1 = new Request("User/signUp" , obj);

        RequestHandler handler = new RequestHandler(request1);
        assertThrows(ActionFailedException.class , handler::handle);

        SignUpDto data2 = new SignUpDto("Hamid" , "12345678@" , "12345678@");

        obj = gson.fromJson(gson.toJson(data2) , JsonObject.class);

        Request request2 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request2);
        Exception exp1 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("userName or password already exists." , exp1.getMessage());


        SignUpDto data3 = new SignUpDto("Amir" , "12345" , "12345");

        obj = gson.fromJson(gson.toJson(data3) , JsonObject.class);

        Request request3 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request3);
        Exception exp2 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("Password must have at least 8 characters." , exp2.getMessage());

        SignUpDto data4 = new SignUpDto("Amin" , "" , "12345");

        obj = gson.fromJson(gson.toJson(data4) , JsonObject.class);

        Request request4 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request4);
        Exception exp3 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("Password must not be empty." , exp3.getMessage());

        SignUpDto data5 = new SignUpDto("" , "123456789@" , "123456789@");

        obj = gson.fromJson(gson.toJson(data5) , JsonObject.class);

        Request request5 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request5);
        Exception exp4 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("User name must not be empty." , exp4.getMessage());

        SignUpDto data6 = new SignUpDto("Mohammad" , "12345678" , "12345678");

        obj = gson.fromJson(gson.toJson(data6) , JsonObject.class);

        Request request6 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request6);
        Exception exp5 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("Password must contain at least one special character." , exp5.getMessage());

        SignUpDto data7 = new SignUpDto("Mahdi" , "Mahdi1234@" , "Mahdi1234@");

        obj = gson.fromJson(gson.toJson(data7) , JsonObject.class);

        Request request7 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request7);
        Exception exp6 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("Password must not contain user name." , exp6.getMessage());

        SignUpDto data8 = new SignUpDto("Helia" , "12345678910@" , "12345");

        obj = gson.fromJson(gson.toJson(data8) , JsonObject.class);

        Request request8 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request8);
        Exception exp7 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("Confirm password does not match password." , exp7.getMessage());

        LogInDto data9 = new LogInDto("Ali" , "12345678@");

        obj = gson.fromJson(gson.toJson(data9) , JsonObject.class);

        Request request9 = new Request("User/logIn" , obj);
        handler = new RequestHandler(request9);
        assertThrows(ActionFailedException.class , handler::handle);

        LogInDto data10 = new LogInDto("Mosh" , "12345678@");

        obj = gson.fromJson(gson.toJson(data10) , JsonObject.class);

        Request request10 = new Request("User/logIn" , obj);
        handler = new RequestHandler(request10);
        Exception exp8 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("User wasn't found." , exp8.getMessage());

        User user1 = UserService.getInstance().getUser("Ali" , "99999999@");
        assertEquals(1 , user1.getSessionIds().size());
        LogOutAndRemoveProfilePhotoDto data11 = new LogOutAndRemoveProfilePhotoDto(user1.getSessionIds().stream().findFirst().get());

        obj = gson.fromJson(gson.toJson(data11) , JsonObject.class);
        Request request11 = new Request("User/logOut" , obj);
        handler = new RequestHandler(request11);
        assertDoesNotThrow(handler::handle);
        assertEquals(0 , user1.getSessionIds().size());

        LogInDto data = new LogInDto("Ali" , "99999999@");

        obj = gson.fromJson(gson.toJson(data) , JsonObject.class);
        Request request = new Request("User/logIn" , obj);
        handler = new RequestHandler(request);

        assertDoesNotThrow(handler::handle);

        ChangePasswordDto data12 = new ChangePasswordDto(user1.getSessionIds().stream().findFirst().get() , "99999999@" , "11111111@" , "11111111@");

        obj = gson.fromJson(gson.toJson(data12) , JsonObject.class);
        Request request12 = new Request("User/changePassword" , obj);
        handler = new RequestHandler(request12);

        assertDoesNotThrow(handler::handle);
        assertEquals("11111111@" , user1.getPassword());

        ChangePasswordDto data13 = new ChangePasswordDto(user1.getSessionIds().stream().findFirst().get() , "11111111@" , "99999999@" ,"99999999@");

        obj = gson.fromJson(gson.toJson(data13) , JsonObject.class);
        Request request13 = new Request("User/changePassword" , obj);
        handler = new RequestHandler(request13);
        assertDoesNotThrow(handler::handle);

        ChangePasswordDto data14 = new ChangePasswordDto(user1.getSessionIds().stream().findFirst().get() , "12345678@" , "@12345678" , "@12345678");

        obj = gson.fromJson(gson.toJson(data14) , JsonObject.class);
        Request request14 = new Request("User/changePassword" , obj);
        handler = new RequestHandler(request14);
        assertThrows(ActionFailedException.class , handler::handle);

        AddProfilePhotoDto data15 = new AddProfilePhotoDto(user1.getSessionIds().stream().findFirst().get() , "1234");

        obj = gson.fromJson(gson.toJson(data15) , JsonObject.class);
        Request request15 = new Request("User/addProfilePhoto" , obj);
        handler = new RequestHandler(request15);
        assertDoesNotThrow(handler::handle);

        assertEquals("1234" , user1.getProfilePhotoId());

        LogOutAndRemoveProfilePhotoDto data16 = new LogOutAndRemoveProfilePhotoDto(user1.getSessionIds().stream().findFirst().get());

        obj = gson.fromJson(gson.toJson(data16) , JsonObject.class);
        Request request16 = new Request("User/removeProfilePhoto" , obj);
        handler = new RequestHandler(request16);
        assertDoesNotThrow(handler::handle);

        assertNull(user1.getProfilePhotoId());

        SignUpDto data17 = new SignUpDto("John" , "@87654321@" , "@87654321@");

        obj = gson.fromJson(gson.toJson(data17) , JsonObject.class);
        Request request17 = new Request("User/signUp" , obj);
        handler = new RequestHandler(request17);
        assertThrows(ActionFailedException.class , handler::handle);

        User user2 = UserService.getInstance().getUser("John" , "@87654321@");

        FollowAndUnfollowDto data18 = new FollowAndUnfollowDto(user1.getSessionIds().stream().findAny().get() , user2.getId());

        obj = gson.fromJson(gson.toJson(data18) , JsonObject.class);
        Request request18 = new Request("User/follow" , obj);
        handler = new RequestHandler(request18);

        assertDoesNotThrow(handler::handle);
        assertEquals(1 , user1.getFollowingsId().size());
        assertEquals(1 , user2.getFollowersId().size());

        Request request19 = new Request("User/unfollow" , obj);
        handler = new RequestHandler(request19);

        assertDoesNotThrow(handler::handle);
        assertEquals(0 , user1.getFollowingsId().size());
        assertEquals(0 , user2.getFollowersId().size());
    }
}
