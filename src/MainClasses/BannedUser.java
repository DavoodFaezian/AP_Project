package MainClasses;

public class BannedUser extends BaseClass<BannedUser> {
    private String userId;
    private boolean isUserAllowedToLogin;
    private boolean isUserAllowedToComment;
    private boolean isUserAllowedToPost;

    public BannedUser(String userId, boolean isUserAllowedToLogin, boolean isUserAllowedToComment, boolean isUserAllowedToPost) {
        this.userId = userId;
        this.isUserAllowedToLogin = isUserAllowedToLogin;
        this.isUserAllowedToComment = isUserAllowedToComment;
        this.isUserAllowedToPost = isUserAllowedToPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isUserAllowedToComment() {
        return isUserAllowedToComment;
    }

    public void setUserAllowedToComment(boolean userAllowedToComment) {
        isUserAllowedToComment = userAllowedToComment;
    }

    public boolean isUserAllowedToLogin() {
        return isUserAllowedToLogin;
    }

    public void setUserAllowedToLogin(boolean userAllowedToLogin) {
        isUserAllowedToLogin = userAllowedToLogin;
    }

    public boolean isUserAllowedToPost() {
        return isUserAllowedToPost;
    }

    public void setUserAllowedToPost(boolean userAllowedToPost) {
        isUserAllowedToPost = userAllowedToPost;
    }


}
