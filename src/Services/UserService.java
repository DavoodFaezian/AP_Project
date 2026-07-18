package Services;

import DTO.LogInDto;
import DTO.CheckPasswordDto;
import DTO.SignUpDto;
import Exceptions.*;
import MainClasses.Session;
import MainClasses.User;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.List;
import java.util.regex.Pattern;

public class UserService {

    private static final UserService instance = new UserService();

    private UserService() {
    }

    public static UserService getInstance() {
        return instance;
    }

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        if(userName.isEmpty()){
            throw new FieldIsEmptyException("User name must not be empty!!!" , userName);
        }
    }

    private void validatePassword(String password){
        if(password.isEmpty()){
            throw new FieldIsEmptyException("Password must not be empty!!!" , password);
        }
    }

    private void validateLength(String password){
        if(password.length() < MIN_LENGTH){
            throw new PasswordNotLongEnoughException("Password must have at least 8 characters!!!");
        }
    }

    private void validateStrength(String password){
        if(!Pattern.compile("[!@#$%^&*+=_?]").matcher(password).find()){
            throw new PasswordNotStrongException("Password must contain at least one special character!!!");
        }
    }

    private void validateDoesNotContainUserName(String userName , String password){
        if(password.contains(userName)){
            throw new PasswordContainsUserNameException("Password must not contain user name!!!");
        }
    }

    public void validateUser(String userName , String password , String actionName){
        List<User> users = UserRepository.getInstance().getAllUsers();
        boolean isUserNotValid = users.stream().anyMatch(s -> s.getUserName().equals(userName) || s.getUserName().equals(password));
        if (isUserNotValid) {
            throw new ActionFailedException(actionName);
        }
    }

    public void validateOldPassword(User user , String oldPassword , String actionName) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new ActionFailedException(actionName);
        }
    }

    public void validatePassword(String oldPassword , String newPassword , String actionName) {
        if (!oldPassword.equals(newPassword)) {
            throw new ActionFailedException(actionName);
        }
    }

    public void validateSignUp(String userName , String password , String repeatedPassword) {
        validateUserName(userName);
        validatePassword(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(userName , password);
        validatePassword(password , repeatedPassword , "confirmPassword");
        validateUser(userName , password , "sign up");
    }

    public void validateLogIn(User user , String userName , String password , String repeatedPassword) {
        if(!user.getUserName().equals(userName) || user.getPassword().equals(password) || !password.equals(repeatedPassword)) {
            throw new ActionFailedException("log in");
        }
    }

    public void signUp(SignUpDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        validateSignUp(userName , password , repeatedPassword);
        User user = UserRepository.getInstance().create(userName , password);
        Session session = SessionRepository.getInstance().createSession(user.getId());
        user.getSessions().add(session);
    }

    public void logIn(LogInDto data) {
        String userId = data.getUserId();
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        User user = UserRepository.getInstance().findUserById(userId);
        validateLogIn(user , userName , password, repeatedPassword);
        Session session = SessionRepository.getInstance().createSession(userId);
        user.getSessions().add(session);
    }

    public void logOut(String userId) {

    }

    public void changePassword(String userId , String oldPassword , String newPassword) {
        User user = UserRepository.getInstance().findUserById(userId);
        validateOldPassword(user , oldPassword , "changePassword");
        validatePassword(oldPassword , newPassword , "logIn");
        user.setPassword(newPassword);
    }
}
