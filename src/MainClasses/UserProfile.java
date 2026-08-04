package MainClasses;

import java.util.HashSet;
import java.util.Set;

public class UserProfile extends BaseClass<UserProfile> {
    private String userId;
    private String profilePhotoId;
    private Theme theme = Theme.SYSTEM;

    private Set<String> sessionIds = new HashSet<>();

    private Set<String> followersId = new HashSet<>();

    private Set<String> followingsId = new HashSet<>();
    public UserProfile(String userId) {
        this.userId = userId;
    }

    public UserProfile(String userId, String profilePhotoId, Theme theme, Set<String> sessionIds, Set<String> followersId, Set<String> followingsId) {
        this.userId = userId;
        this.profilePhotoId = profilePhotoId;
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

    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
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
