import MainClasses.Theme;
import MainClasses.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    private User user1;
    private User user2;

    @BeforeEach
    public void before(){
        user1 = new User("" , "123456" , Theme.LIGHT);
        user2 = new User("Davood" , "123456789*" , Theme.DARK);
    }

    @Test
    public void passwordTest(){

    }
}
