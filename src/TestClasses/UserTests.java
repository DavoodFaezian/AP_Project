import APIServer.Request;
import RequestHandler.RequestHandler;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    public void signUpTest() {

        JsonObject obj = new JsonObject();
        obj.addProperty("Username" , "Ali");
        obj.addProperty("password" , "12345678@");

        Request request = new Request("User/signUp" , obj);

        RequestHandler handler = new RequestHandler(request);
        assertDoesNotThrow(handler::handle);
    }
}
