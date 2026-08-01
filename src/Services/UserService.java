package Services;


import DTO.User.*;
import Exceptions.*;
import MainClasses.Session;
import MainClasses.User;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.regex.Pattern;

public class UserService {

    private static final UserService instance = new UserService();

    public static UserService getInstance() {
        return instance;
    }

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        if(userName.isEmpty()){
            throw new ActionFailedException("User name must not be empty.");
        }
    }

    private void validateNotEmpty(String password){
        if(password.isEmpty()){
            throw new ActionFailedException("Password must not be empty.");
        }
    }

    private void validateLength(String password){
        if(password.length() < MIN_LENGTH){
            throw new ActionFailedException("Password must have at least 8 characters.");
        }
    }

    private void validateStrength(String password){
        if(!Pattern.compile("[!@#$%^&*+=_?]").matcher(password).find()){
            throw new ActionFailedException("Password must contain at least one special character.");
        }
    }

    private void validateDoesNotContainUserName(String userName , String password){
        if(password.contains(userName)){
            throw new ActionFailedException("Password must not contain user name.");
        }
    }

    public void validateConfirmPassword(String password , String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ActionFailedException("Confirm password does not match password.");
        }
    }

    public void validatePassword(String userName , String password) {
        validateNotEmpty(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(userName , password);
    }

    public void validateSignUp(String userName , String password , String confirmPassword) {
        validateUserName(userName);
        validatePassword(userName , password);
        UserRepository.getInstance().checkUserNameAndPassword(userName , password);
        validateConfirmPassword(password , confirmPassword);
    }

    public User validateLogIn(String userName , String password) {
        return UserRepository.getInstance().findUserByUserNameAndPassword(userName , password);
    }

    public void validateOldPassword(User user , String oldPassword) {
        if (!user.getPassword().equals(oldPassword)) {
            throw new ActionFailedException("Old password is incorrect.");
        }
    }

    public void validateNewPassword(String password , String newPassword) {
        if(!password.equals(newPassword)) {
            throw new ActionFailedException("Verify password failed.");
        }
    }

    public void signUp(SignUpDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        validateSignUp(userName , password , repeatedPassword);
        User user = UserRepository.getInstance().create(userName , password);
        Session session = SessionRepository.getInstance().createSession(user.getId());
        user.getSessionIds().add(session.getId());
        UserRepository.getInstance().update();
    }

    public String logIn(LogInDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();
        User user = validateLogIn(userName , password);
        Session session = SessionRepository.getInstance().createSession(user.getId());
        user.getSessionIds().add(session.getId());
        UserRepository.getInstance().update();
        return session.getId();
    }

    public void logOut(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        SessionRepository.getInstance().removeSessions(user);
        UserRepository.getInstance().update();
    }

    public void changePassword(ChangePasswordDto data) {
        String sessionId = data.getSessionId();
        String oldPassword = data.getOldPassword();
        String newPassword = data.getNewPassword();
        String confirmNewPassword = data.getConfirmNewPassword();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        validateOldPassword(user , oldPassword);
        validatePassword(user.getUserName() , newPassword);
        validateNewPassword(newPassword , confirmNewPassword);
        user.setPassword(newPassword);
        UserRepository.getInstance().update();
    }

    public void addProfilePhoto(AddProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        String profilePhotoId = data.getProfilePhotoId();
       User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
       user.setProfilePhotoId(profilePhotoId);
       UserRepository.getInstance().update();
    }

    public void removeProfilePhoto(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        user.setProfilePhotoId(null);
        UserRepository.getInstance().update();
    }

    public void follow(FollowAndUnfollowDto data) {
        String followerSessionId = data.getFollowerSessionId();
        String followingUserId = data.getFollowingUserId();
        User follower = SessionRepository.getInstance().findUserBySessionId(followerSessionId);
        User following = UserRepository.getInstance().findUserById(followingUserId);
        following.getFollowersId().add(follower.getId());
        follower.getFollowingsId().add(following.getId());
        UserRepository.getInstance().update();
    }

    public void unfollow(FollowAndUnfollowDto data) {
        String followerSessionId = data.getFollowerSessionId();
        String followingUserId = data.getFollowingUserId();
        User follower = SessionRepository.getInstance().findUserBySessionId(followerSessionId);
        User following = UserRepository.getInstance().findUserById(followingUserId);
        following.getFollowersId().remove(follower.getId());
        follower.getFollowingsId().remove(following.getId());
        UserRepository.getInstance().update();
    }

    public User getUser(String userName , String password) {
        return UserRepository.getInstance().findUserByUserNameAndPassword(userName , password);
    }
}
