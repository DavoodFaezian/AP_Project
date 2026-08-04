package DTO.User;

public class UserDataDto {

    private String userId;

    private String userName;

    private String photoProfileId;

    public UserDataDto(String userId, String userName, String photoProfileId) {
        this.userId = userId;
        this.userName = userName;
        this.photoProfileId = photoProfileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoProfileId() {
        return photoProfileId;
    }

    public void setPhotoProfileId(String photoProfileId) {
        this.photoProfileId = photoProfileId;
    }
}
