package Services;

import Exceptions.*;
import MainClasses.User;
import Repositories.UserRepository;

import javax.swing.*;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {

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

    public void userValidtor(String userName , String password , String actionName){
        List<User> users = UserRepository.getInstance().getAllUsers();
        boolean isUserNotValid = users.stream().anyMatch(s -> s.getUserName().equals(userName) || s.getUserName().equals(password));
        if (isUserNotValid) {
            throw new ActionFailedException(actionName);
        }
    }

    public void validateOldPassword (User user , String oldPassword , String actionName) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new ActionFailedException(actionName);
        }
    }

    public void validatePassword (String oldPassword , String newPassword , String actionName) {
        if (!oldPassword.equals(newPassword)) {
            throw new ActionFailedException(actionName);
        }
    }

    public void signUp(String userName , String password) {
        validateUserName(userName);
        validatePassword(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(userName , password);
        userValidtor(userName , password , "sign up");
    }

    public void logIn(String userName , String password) {

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
