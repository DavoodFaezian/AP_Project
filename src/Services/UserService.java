package Services;

import DTO.ChangePasswordDto;
import DTO.LogInDto;
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

    public static UserService getInstance() {
        return instance;
    }

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        if(userName.isEmpty()){
            throw new ActionFailedException("User name must not be empty!!");
        }
    }

    private void validatePassword(String password){
        if(password.isEmpty()){
            throw new ActionFailedException("Password must not be empty!!!");
        }
    }

    private void validateLength(String password){
        if(password.length() < MIN_LENGTH){
            throw new ActionFailedException("Password must have at least 8 characters!!!");
        }
    }

    private void validateStrength(String password){
        if(!Pattern.compile("[!@#$%^&*+=_?]").matcher(password).find()){
            throw new ActionFailedException("Password must contain at least one special character!!!");
        }
    }

    private void validateDoesNotContainUserName(String userName , String password){
        if(password.contains(userName)){
            throw new ActionFailedException("Password must not contain user name!!!");
        }
    }

    public void validateUser(String userName , String password){
        List<User> users = UserRepository.getInstance().getAllUsers();
        boolean isUserNotValid = users.stream().anyMatch(s -> s.getUserName().equals(userName) || s.getUserName().equals(password));
        if (isUserNotValid) {
            throw new ActionFailedException("userName or password already exists.");
        }
    }

    public void validateConfirmPassword(String password , String repeatedPassword) {
        if (!password.equals(repeatedPassword)) {
            throw new ActionFailedException("confirm password does not match password");
        }
    }

    public void validatePassword(String userName , String password) {
        validatePassword(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(userName , password);
    }

    public void validateSignUp(String userName , String password , String repeatedPassword) {
        validateUserName(userName);
        validatePassword(userName , password);
        validatePassword(password , repeatedPassword);
        validateUser(userName , password);
    }

    public void validateLogIn(User user , String userName , String password , String repeatedPassword) {
        if(!user.getUserName().equals(userName) || user.getPassword().equals(password) || !password.equals(repeatedPassword)) {
            throw new ActionFailedException("log in");
        }
    }

    public void validateOldPassword(User user , String oldPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new ActionFailedException("Old password is not correct");
        }
    }

    public void validateNewPassword(String password , String newPassword) {
        if(!password.equals(newPassword)) {
            throw new ActionFailedException("Verify password failed");
        }
    }

    public void signUp(SignUpDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        validateSignUp(userName , password , repeatedPassword);
        User user = UserRepository.getInstance().create(userName , password);
        Session session = SessionRepository.getInstance().createSession(user.getId());
        user.getSessions().add(session.getId());
    }

    public void logIn(LogInDto data) {
        String userId = data.getUserId();
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        User user = UserRepository.getInstance().findUserById(userId);
        validateLogIn(user , userName , password , repeatedPassword);
        Session session = SessionRepository.getInstance().createSession(userId);
        user.getSessions().add(session.getId());
    }

    public void logOut(String userId) {

    }

    public void changePassword(ChangePasswordDto data) {
        String userId = data.getUserId();
        String oldPassword = data.getOldPassword();
        String newPassword = data.getNewPassword();
        String confirmNewPassword = data.getConfirmNewPassword();
        User user = UserRepository.getInstance().findUserById(userId);
        validateOldPassword(user , oldPassword);
        validatePassword(user.getUserName() , newPassword);
        validateNewPassword(newPassword , confirmNewPassword);
        user.setPassword(newPassword);
    }
}
