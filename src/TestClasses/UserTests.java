import APIServer.Request;
import Dto.LogInDto;
import Dto.LogOutDto;
import Dto.SignUpDto;
import Exceptions.ActionFailedException;
import MainClasses.User;
import RequestHandler.RequestHandler;
import Services.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

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
        assertDoesNotThrow(handler::handle);

        LogInDto data10 = new LogInDto("Mosh" , "12345678@");

        obj = gson.fromJson(gson.toJson(data10) , JsonObject.class);

        Request request10 = new Request("User/logIn" , obj);
        handler = new RequestHandler(request10);
        Exception exp8 = assertThrows(ActionFailedException.class , handler::handle);
        assertEquals("User wasn't found." , exp8.getMessage());

        User user = UserService.getInstance().getUser("Ali" , "12345678@");
        assertEquals(1 , user.getSessionIds().size());
        LogOutDto data11 = new LogOutDto(user.getSessionIds().stream().findFirst().get());

        obj = gson.fromJson(gson.toJson(data11) , JsonObject.class);
        Request request11 = new Request("User/logOut" , obj);
        handler = new RequestHandler(request11);
        assertDoesNotThrow(handler::handle);
        assertEquals(0 , user.getSessionIds().size());



    }
}
