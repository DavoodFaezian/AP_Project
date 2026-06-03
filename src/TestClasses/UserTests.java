import Exceptions.FieldIsEmptyException;
import Exceptions.PasswordContainsUserNameException;
import Exceptions.PasswordNotLongEnoughException;
import Exceptions.PasswordNotStrongException;
import MainClasses.Theme;
import MainClasses.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    public void passwordTest(){
        assertThrows(FieldIsEmptyException.class , () -> {new User("Ali" , "" , Theme.LIGHT);});
        assertThrows(PasswordNotLongEnoughException.class , () -> {new User("Davood" , "123" , Theme.LIGHT);});
        assertThrows(PasswordNotStrongException.class , () -> {new User("Mahdi" , "123456789" , Theme.LIGHT);});
        assertThrows(PasswordContainsUserNameException.class , () -> {new User("Amin" , "Amin1234567*" , Theme.LIGHT);});
        assertDoesNotThrow(() -> {new User("Hamid" , "123456#45" , Theme.LIGHT);});
    }
}
