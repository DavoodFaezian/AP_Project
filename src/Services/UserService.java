package Services;


import DTO.User.*;
import Exceptions.*;
import MainClasses.Session;
import MainClasses.User;
import MainClasses.UserProfile;
import Repositories.SessionRepository;
import Repositories.UserProfileRepository;
import Repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final UserService instance = new UserService();
    private final UserProfileRepository userProfileRepository = UserProfileRepository.getInstance();

    public static UserService getInstance() {
        return instance;
    }

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        if(userName == null){
            throw new ActionFailedException("User name must not be null.");
        }
        if(userName.isEmpty()){
            throw new ActionFailedException("User name must not be empty.");
        }
    }

    private void validateNotEmpty(String password){
        if(password == null){
            throw new ActionFailedException("Password must not be null.");

        }
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
        if(confirmPassword == null){
            throw new ActionFailedException("Confirm password cannot be null.");
        }
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
        UserRepository.getInstance().checkUserNameAndPassword(userName);
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

    public String signUp(SignUpDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();

        validateSignUp(userName, password, data.getRepeatedPassword());

        User user = UserRepository.getInstance().create(userName, password);
        userProfileRepository.createUserProfile(user.getId());

        return getString(user);
    }

    private String getString(User user) {
        Session session = SessionRepository.getInstance().createSession(user.getId());

        UserProfile profile = userProfileRepository.getUserProfileByUserId(user.getId())
                .orElseThrow(() -> new ItemNotFoundException("user profile", user.getId()));

        profile.getSessionIds().add(session.getId());
        userProfileRepository.updateUserProfile(profile);
        return session.getId();
    }


    public String logIn(LogInDto data) {
        User user = validateLogIn(data.getUserName(), data.getPassword());
        return getString(user);
    }

    public void logOut(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);


        userProfileRepository.getUserProfileByUserId(user.getId()).ifPresent(profile -> {
            profile.getSessionIds().remove(sessionId);
            userProfileRepository.updateUserProfile(profile);
            SessionRepository.getInstance().removeSessions(profile);

        });
    }

    public void changePassword(ChangePasswordDto data) {
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());

        validateOldPassword(user, data.getOldPassword());
        validatePassword(user.getUserName(), data.getNewPassword());
        validateNewPassword(data.getNewPassword(), data.getConfirmNewPassword());

        user.setPassword(data.getNewPassword());
        UserRepository.getInstance().update();
    }


    public void addProfilePhoto(AddProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        String profilePhotoId = data.getProfilePhotoId();
       User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
       Optional<UserProfile> userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
       if(userProfile.isPresent()){
           userProfile.get().setProfilePhotoId(profilePhotoId);
           userProfileRepository.updateUserProfile(userProfile.get());
       }
    }

    public void removeProfilePhoto(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Optional<UserProfile> userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
        if(userProfile.isPresent()){
            userProfile.get().setProfilePhotoId(null);
            userProfileRepository.updateUserProfile(userProfile.get());
        }
    }

    public void follow(FollowAndUnfollowDto data) {
        User follower = SessionRepository.getInstance()
                .findUserBySessionId(data.getFollowerSessionId());

        String followingUserId = data.getFollowingUserId();

        if (follower.getId().equals(followingUserId)) {
            throw new ActionFailedException("You cannot follow yourself.");
        }

        UserProfile followerProfile = userProfileRepository
                .getUserProfileByUserId(follower.getId())
                .orElseThrow(() -> new ItemNotFoundException("user profile", follower.getId()));

        UserProfile followingProfile = userProfileRepository
                .getUserProfileByUserId(followingUserId)
                .orElseThrow(() -> new ItemNotFoundException("user profile", followingUserId));

        followerProfile.getFollowingsId().add(followingUserId);
        followingProfile.getFollowersId().add(follower.getId());

        userProfileRepository.updateUserProfile(followerProfile);
        userProfileRepository.updateUserProfile(followingProfile);
    }

    public void unfollow(FollowAndUnfollowDto data) {
        User follower = SessionRepository.getInstance()
                .findUserBySessionId(data.getFollowerSessionId());

        String followingUserId = data.getFollowingUserId();

        UserProfile followerProfile = userProfileRepository
                .getUserProfileByUserId(follower.getId())
                .orElseThrow(() -> new ItemNotFoundException("user profile", follower.getId()));

        UserProfile followingProfile = userProfileRepository
                .getUserProfileByUserId(followingUserId)
                .orElseThrow(() -> new ItemNotFoundException("user profile", followingUserId));

        followerProfile.getFollowingsId().remove(followingUserId);
        followingProfile.getFollowersId().remove(follower.getId());

        userProfileRepository.updateUserProfile(followerProfile);
        userProfileRepository.updateUserProfile(followingProfile);
    }

    public List<User> searchUsers(SearchUsersDto data) {
        String sessionId = data.getSessionId();
        SessionRepository.getInstance().findUserBySessionId(sessionId);
        String query = data.getQuery();
        String cleanQuery = query.trim().toLowerCase();
        return UserRepository.getInstance().filterUsers(
                user -> user.getUserName().contains(cleanQuery)
        );
    }

    public void changeTheme(ChangeThemeDto data) {
        String sessionId  = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(user.getId()).orElseThrow(
                () -> new ItemNotFoundException("user profile", user.getId())
        );
        userProfile.setTheme(data.getTheme());

    }

    public User getUser(String userName , String password) {
        return UserRepository.getInstance().findUserByUserNameAndPassword(userName , password);
    }
}
