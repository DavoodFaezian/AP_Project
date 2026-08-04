package DTO.User;

public class GetUserDto {
    private String userId;
    private String userName;
    private String profilePhotoName;

    public GetUserDto(String userId, String userName, String profilePhotoName) {
        this.userId = userId;
        this.userName = userName;
        this.profilePhotoName = profilePhotoName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePhotoName() {
        return profilePhotoName;
    }
}
