import DTO.SessionIdDto;
import DTO.User.*;
import Exceptions.ActionFailedException;
import MainClasses.Theme;
import MainClasses.User;
import MainClasses.UserProfile;
import Repositories.UserRepository;
import Repositories.UserProfileRepository;
import Services.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    public void userTest() {

        UserService userService = UserService.getInstance();
        UserProfileRepository userProfileRepository = UserProfileRepository.getInstance();

        assertDoesNotThrow(
                () -> {
                    userService.signUp(new SignUpDto("UniqueName", "Unique@1234", "Unique@1234"));
                });

        assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("UniqueName", "Unique1234@", "Unique1234@"));
                });

        assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("MyName", "Unique@1234", "Unique@1234"));
                });

        Exception exp = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("", "Unique1234@", "Unique1234@"));
                });
        assertEquals("User name must not be empty.", exp.getMessage());

        Exception exp1 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("AliSabet", "", "Unique1234@"));
                });
        assertEquals("Password must not be empty.", exp1.getMessage());

        Exception exp2 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("AliSabet", "Unique", "Unique"));
                });
        assertEquals("Password must have at least 8 characters.", exp2.getMessage());

        Exception exp3 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("AliSabet", "Unique1234", "Unique1234"));
                });
        assertEquals("Password must contain at least one special character.", exp3.getMessage());

        Exception exp4 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("Ali", "Ali1234@", "Ali1234@"));
                });
        assertEquals("Password must not contain user name.", exp4.getMessage());

        Exception exp5 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.signUp(new SignUpDto("AliSabet", "Ali1234@", "Ali1234@t"));
                });
        assertEquals("Confirm password does not match password.", exp5.getMessage());

        assertThrows(ActionFailedException.class,
                () -> {
                    userService.logIn(new LogInDto("AliSabet", "iehtwgh"));
                });

        User user1 = userService.getUserMainClass("UniqueName", "Unique@1234");

        String sessionId = userService.logIn(new LogInDto("UniqueName", "Unique@1234")).getId();

        assertDoesNotThrow(
                () -> {
                    userService.logOut(
                            new LogOutAndRemoveProfilePhotoDto(
                                    sessionId
                            )
                    );
                }
        );

        user1 = UserRepository.getInstance().findUserById(user1.getId());

        UserProfile profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();

        assertEquals(0, profile1.getSessionIds().size());

        final String sessionId1 = userService.logIn(
                new LogInDto("UniqueName", "Unique@1234")
        ).getId();

        user1 = UserRepository.getInstance().findUserById(user1.getId());
        profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();

        assertEquals(1, profile1.getSessionIds().size());


        assertThrows(ActionFailedException.class,
                () -> {
                    userService.logIn(new LogInDto("MyName", "Unique@1234"));
                });

        Exception exp6 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "iksnbgeo", "Ali123@", "Ali1234@"));
                });
        assertEquals("Old password is incorrect.", exp6.getMessage());

        Exception exp7 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "Unique@1234", "Ali", "Ali"));
                });
        assertEquals("Password must have at least 8 characters.", exp7.getMessage());

        Exception exp8 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "Unique@1234", "Ali1234567", "Ali1234567"));
                });
        assertEquals("Password must contain at least one special character.", exp8.getMessage());

        Exception exp9 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "Unique@1234", "UniqueName@100", "UniqueName@100"));
                });
        assertEquals("Password must not contain user name.", exp9.getMessage());

        Exception exp10 = assertThrows(ActionFailedException.class,
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "Unique@1234", "Unique_Name@100", "Unique_Name@"));
                });
        assertEquals("Confirm password does not match password.", exp10.getMessage());

        assertDoesNotThrow(
                () -> {
                    userService.changePassword(new ChangePasswordDto(sessionId1, "Unique@1234", "Unique_1234@", "Unique_1234@"));

                }
        );

        user1 = UserRepository.getInstance().findUserById(user1.getId());

        assertEquals("Unique_1234@", user1.getPassword());

        assertDoesNotThrow(
                () -> {
                    userService.signUp(new SignUpDto("Unique_Name", "Unique@1234", "Unique@1234"));
                });

        User user2 = userService.getUserMainClass("Unique_Name", "Unique@1234");

        userService.follow(
                new FollowAndUnfollowDto(
                        sessionId1, user2.getId()
                )
        );

        profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();
        UserProfile profile2 = userProfileRepository.getUserProfileByUserId(user2.getId()).orElseThrow();

        assertEquals(1, profile1.getFollowingsId().size());
        assertEquals(1, profile2.getFollowersId().size());

        assertTrue(
                UserService.getInstance().checkIsFollowing(
                        new GetUserByIdDto(
                                sessionId1, user2.getId()
                        )
                ).isValue()
        );


        userService.unfollow(
                new FollowAndUnfollowDto(
                        sessionId1, user2.getId()
                )
        );

        profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();
        profile2 = userProfileRepository.getUserProfileByUserId(user2.getId()).orElseThrow();

        assertEquals(0, profile1.getFollowingsId().size());
        assertEquals(0, profile2.getFollowersId().size());

        assertFalse(
                UserService.getInstance().checkIsFollowing(
                        new GetUserByIdDto(
                                sessionId1, user2.getId()
                        )
                ).isValue()
        );

        userService.addProfilePhoto(
                new AddProfilePhotoDto(
                        sessionId1, "1234"
                ));

        profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();

        assertEquals("1234", profile1.getProfilePhotoName());

        assertEquals(
                "1234", UserService.getInstance().getUserProfile(
                        new GetUserByIdDto(
                                sessionId1, user1.getId()
                        )
                ).getProfilePhotoName()
        );

        assertEquals(
                "UniqueName", UserService.getInstance().getUserProfile(
                        new GetUserByIdDto(
                                sessionId1, user1.getId()
                        )
                ).getUserName()
        );

        assertEquals(
                "1234", UserService.getInstance().getUser(
                        new SessionIdDto(
                                sessionId1
                        )
                ).getPhotoName()
        );

        assertEquals(
                "UniqueName", UserService.getInstance().getUser(
                        new SessionIdDto(
                                sessionId1
                        )
                ).getUserName()
        );

        userService.removeProfilePhoto(
                new LogOutAndRemoveProfilePhotoDto(
                        sessionId1
                ));

        profile1 = userProfileRepository.getUserProfileByUserId(user1.getId()).orElseThrow();

        assertNull(profile1.getProfilePhotoName());

        assertDoesNotThrow(
                () -> {
                    UserService.getInstance().changeTheme(
                            new ChangeThemeDto(
                                    sessionId1, Theme.LIGHT
                            )
                    );
                }
        );

        assertEquals(
                Theme.LIGHT, UserService.getInstance().getUser(
                        new SessionIdDto(
                                sessionId1
                        )
                ).getTheme()
        );

        assertEquals(
                1 , UserService.getInstance().searchUsers(
                        new SearchUsersDto(
                                sessionId1 , "Unique_Name"
                        )
                ).getUsers().size()
        );
    }

}
