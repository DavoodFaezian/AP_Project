package Repositories;

import Exceptions.ItemNotFoundException;
import MainClasses.UserProfile;
import java.util.Optional;

public class UserProfileRepository extends BaseRepository<UserProfile> {
    private static final UserProfileRepository instance = new UserProfileRepository();

    private UserProfileRepository() {
        super("user_profiles");
    }

    public static UserProfileRepository getInstance() {
        return instance;
    }
    public void addUserProfile(UserProfile profile) {
        var profileFileManager = getFileManager(profile.getUserId());
        profileFileManager.addToList(profile);
        profileFileManager.save();
    }
    public void removeUserProfile(UserProfile profile) {
        var profileFileManager = getFileManager(profile.getUserId());
        profileFileManager.removeFromList(profile);
        profileFileManager.save();
    }
    public void updateUserProfile(UserProfile profile) {
        var profileFileManager = getFileManager(profile.getUserId());
        profileFileManager.edit(profile);
        profileFileManager.save();
    }

    public UserProfile findUserProfileById(String id, String userId) {
        Optional<UserProfile> profile = getFileManager(userId).findItemById(id);
        if (profile.isEmpty()) {
            throw new ItemNotFoundException("user profile", id);
        }
        return profile.get();
    }
    public Optional<UserProfile> getUserProfileByUserId(String userId) {
        var profileFileManager = getFileManager(userId);
        return profileFileManager.getAll().stream()
                .filter(profile -> profile.getUserId().equals(userId))
                .findFirst();
    }

    public boolean isProfileIdValid(String profileId, String userId) {
        return getFileManager(userId).exists(profile -> profile.getId().equals(profileId));
    }
    public UserProfile createUserProfile(String userId) {
        UserProfile userProfile = new UserProfile(userId);
        addUserProfile(userProfile);
        return userProfile;
    }

}
