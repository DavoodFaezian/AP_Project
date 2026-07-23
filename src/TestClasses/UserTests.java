import APIServer.Request;
import Dto.SignUpDto;
import RequestHandler.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    public void signUpTest() {

        Gson gson = new Gson();

        SignUpDto data = new SignUpDto("Ali" , "12345678@" , "12345678@");

        JsonObject obj = gson.fromJson(gson.toJson(data) , JsonObject.class);

        Request request = new Request("User/signUp" , obj);

        RequestHandler handler = new RequestHandler(request);
        assertDoesNotThrow(handler::handle);
    }
}
