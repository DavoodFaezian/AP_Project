package MainClasses;

import java.util.HashSet;
import java.util.Set;

public class UserProfile extends BaseClass<UserProfile> {
    private String userId;
    private String profilePhotoName;
    private Theme theme = Theme.LIGHT;

    private Set<String> sessionIds = new HashSet<>();

    private Set<String> followersId = new HashSet<>();

    private Set<String> followingsId = new HashSet<>();
    public UserProfile(String userId) {
        this.userId = userId;
    }

    public UserProfile(String userId, String profilePhotoId, Theme theme, Set<String> sessionIds, Set<String> followersId, Set<String> followingsId) {
        this.userId = userId;
        this.profilePhotoName = profilePhotoId;
        this.theme = theme;
        this.sessionIds = sessionIds;
        this.followersId = followersId;
        this.followingsId = followingsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePhotoName() {
        return profilePhotoName;
    }

    public void setProfilePhotoName(String profilePhotoName) {
        this.profilePhotoName = profilePhotoName;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Set<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public Set<String> getFollowersId() {
        return followersId;
    }

    public void setFollowersId(Set<String> followersId) {
        this.followersId = followersId;
    }

    public Set<String> getFollowingsId() {
        return followingsId;
    }

    public void setFollowingsId(Set<String> followingsId) {
        this.followingsId = followingsId;
    }
}
