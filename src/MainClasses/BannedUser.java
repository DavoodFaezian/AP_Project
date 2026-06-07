package MainClasses;

public class BannedUser extends BaseClass<BannedUser> {
    private String userId;
    private User user;
    private boolean isUserAllowedToAddPhotos;
    private boolean isUserAllowedToComment;
    private boolean isUserAllowToShare;

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

    public boolean isUserAllowedToAddPhotos() {
        return isUserAllowedToAddPhotos;
    }

    public void setUserAllowedToAddPhotos(boolean userAllowedToAddPhotos) {
        isUserAllowedToAddPhotos = userAllowedToAddPhotos;
    }

    public boolean isUserAllowToShare() {
        return isUserAllowToShare;
    }

    public void setUserAllowToShare(boolean userAllowToShare) {
        isUserAllowToShare = userAllowToShare;
    }


}
