package MainClasses;

import java.util.*;

public class User extends BaseClass<User>{

    private String userName;

    private String password;

    private String profilePhotoId;

    private Theme theme = Theme.LIGHT;

    private Set<String> sessionIds = new HashSet<>();

    private Set<String> followersId = new HashSet<>();

    private Set<String> followingsId = new HashSet<>();


    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    public Theme getTheme(){
        return theme;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTheme(Theme theme){
        this.theme = theme;
    }


    public void setSessions(Set<String> sessions) {
        this.sessionIds = sessions;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getId() , user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }


}