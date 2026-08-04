package Services;


import Annotaions.ServiceAction;
import DTO.SessionIdDto;
import DTO.StringResultDto;
import DTO.User.*;
import Exceptions.*;
import MainClasses.Session;
import MainClasses.User;
import MainClasses.UserProfile;
import Repositories.BannedUserRepository;
import Repositories.SessionRepository;
import Repositories.UserProfileRepository;
import Repositories.UserRepository;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserService {

    private static final UserService instance = new UserService();
    private final UserProfileRepository userProfileRepository = UserProfileRepository.getInstance();

    @ServiceAction
    public static UserService getInstance() {
        return instance;
    }

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        validateAction(userName == null, "User name must not be null.");
        validateAction(userName.isEmpty(), "User name must not be empty.");
    }

    private void validateNotEmpty(String password){
        validateAction(password == null, "Password must not be null.");
        validateAction(password.isEmpty(), "Password must not be empty.");
    }

    private void validateLength(String password){
        validateAction(password.length() < MIN_LENGTH, "Password must have at least 8 characters.");
    }

    private void validateStrength(String password){
        validateAction(!Pattern.compile("[!@#$%^&*+=_?]").matcher(password).find(), "Password must contain at least one special character.");
    }

    private void validateDoesNotContainUserName(String userName , String password){
        validateAction(password.contains(userName), "Password must not contain user name.");
    }

    private void validateConfirmPassword(String password , String confirmPassword) {
        validateAction(confirmPassword == null, "Confirm password cannot be null.");
        validateAction(!password.equals(confirmPassword), "Confirm password does not match password.");
    }

    private void validatePassword(String userName , String password , String confirmPassword) {
        validateNotEmpty(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(userName , password);
        validateConfirmPassword(password, confirmPassword);
    }

    private void validateSignUp(String userName , String password , String confirmPassword) {
        validateUserName(userName);
        validatePassword(userName , password , confirmPassword);
        UserRepository.getInstance().checkUserNameAndPassword(userName , password);
    }

    private User validateLogIn(String userName , String password) {
        return UserRepository.getInstance().findUserByUserNameAndPassword(userName , password);
    }

    private void validatePermission(User user) {
        BannedUserRepository.getInstance().isUserAllowedToLogin(user.getId());
    }

    private void validateOldPassword(User user , String oldPassword) {
        validateAction(!user.getPassword().equals(oldPassword), "Old password is incorrect.");
    }

    @ServiceAction
    public StringResultDto signUp(SignUpDto data) {
        String userName = data.getUserName();
        String password = data.getPassword();
        String repeatedPassword = data.getRepeatedPassword();
        validateSignUp(userName, password, repeatedPassword);

        User user = UserRepository.getInstance().create(userName, password);
        userProfileRepository.createUserProfile(user.getId());

        return getResult(user);
    }

    @ServiceAction
    public StringResultDto logIn(LogInDto data) {
        User user = validateLogIn(data.getUserName(), data.getPassword());
        validatePermission(user);
        return getResult(user);
    }

    private StringResultDto getResult(User user) {
        Session session = SessionRepository.getInstance().createSession(user.getId());

        UserProfile profile = validateUserProfile(user.getId());

        profile.getSessionIds().add(session.getId());
        userProfileRepository.updateUserProfile(profile);

        return new StringResultDto(session.getId());
    }

    @ServiceAction
    public void changeUserName(ChangeUserNameDto data){
        validateUserName(data.getNewUserName());
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        user.setUserName(data.getNewUserName());
        UserRepository.getInstance().editUser(user);
    }

    @ServiceAction
    public void logOut(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);


        userProfileRepository.getUserProfileByUserId(user.getId()).ifPresent(profile -> {
            profile.getSessionIds().remove(sessionId);
            userProfileRepository.updateUserProfile(profile);
            SessionRepository.getInstance().removeSessions(profile);

        });
    }

    @ServiceAction
    public void changePassword(ChangePasswordDto data) {
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());

        validateOldPassword(user, data.getOldPassword());
        validatePassword(user.getUserName(), data.getNewPassword(), data.getConfirmNewPassword());

        user.setPassword(data.getNewPassword());
        UserRepository.getInstance().update();
    }

    @ServiceAction
    public void addProfilePhoto(AddProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        String profilePhotoId = data.getProfilePhotoId();
       User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
       Optional<UserProfile> userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
       if(userProfile.isPresent()){
           userProfile.get().setProfilePhotoName(profilePhotoId);
           userProfileRepository.updateUserProfile(userProfile.get());
       }
    }

    @ServiceAction
    public void removeProfilePhoto(LogOutAndRemoveProfilePhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Optional<UserProfile> userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
        if(userProfile.isPresent()){
            userProfile.get().setProfilePhotoName(null);
            userProfileRepository.updateUserProfile(userProfile.get());
        }
    }

    @ServiceAction
    public void follow(FollowAndUnfollowDto data) {
        User follower = SessionRepository.getInstance()
                .findUserBySessionId(data.getFollowerSessionId());

        String followingUserId = data.getFollowingUserId();

        validateAction(follower.getId().equals(followingUserId), "You cannot follow yourself.");

        UserProfile followerProfile = validateUserProfile(follower.getId());

        UserProfile followingProfile = validateUserProfile(followingUserId);

        followerProfile.getFollowingsId().add(followingUserId);
        followingProfile.getFollowersId().add(follower.getId());

        userProfileRepository.updateUserProfile(followerProfile);
        userProfileRepository.updateUserProfile(followingProfile);
    }

    private UserProfile validateUserProfile(String follower) {
        return userProfileRepository
                .getUserProfileByUserId(follower)
                .orElseThrow(() -> new ItemNotFoundException("user profile", follower));
    }

    private static void validateAction(boolean condition, String actionName) {
        if (condition) {
            throw new ActionFailedException(actionName);
        }
    }

    @ServiceAction
    public void unfollow(FollowAndUnfollowDto data) {
        User follower = SessionRepository.getInstance()
                .findUserBySessionId(data.getFollowerSessionId());

        String followingUserId = data.getFollowingUserId();

        UserProfile followerProfile = validateUserProfile(follower.getId());

        UserProfile followingProfile = validateUserProfile(followingUserId);

        followerProfile.getFollowingsId().remove(followingUserId);
        followingProfile.getFollowersId().remove(follower.getId());

        userProfileRepository.updateUserProfile(followerProfile);
        userProfileRepository.updateUserProfile(followingProfile);
    }


    public User getUserMainClass(String userName , String password) {
        return UserRepository.getInstance().findUserByUserNameAndPassword(userName , password);
    }

    @ServiceAction
    public void changeTheme(ChangeThemeDto data){
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());
        Optional<UserProfile> profile = userProfileRepository.getUserProfileByUserId(user.getId());
        validateAction(profile.isEmpty(), "Oops! profile was not found.");
        UserProfile notNullProfile = profile.get();
        notNullProfile.setTheme(data.getTheme());
        userProfileRepository.updateUserProfile(notNullProfile);

    }

    @ServiceAction
    public UserProfileDto getUser(SessionIdDto data){
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());
        Optional<UserProfile> profile = userProfileRepository.getUserProfileByUserId(user.getId());
        validateAction(profile.isEmpty(), "Ops! profile was not found.");
        UserProfile notNullProfile = profile.get();
        return new UserProfileDto(
                user.getUserName(),
                notNullProfile.getProfilePhotoName(),
                notNullProfile.getTheme()
        );

    }
}
