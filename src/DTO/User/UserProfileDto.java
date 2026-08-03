package DTO.User;

import MainClasses.Theme;

public class UserProfileDto {

    private String userName;
    private String photoName;
    private Theme theme;

    public UserProfileDto(String userName, String profilePhotoName, Theme theme) {
        this.userName = userName;
        this.photoName = profilePhotoName;
        this.theme = theme;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public Theme getTheme() {
        return theme;
    }
}
