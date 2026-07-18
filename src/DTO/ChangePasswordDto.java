package DTO;

import com.google.gson.JsonObject;

public class ChangePasswordDto {

    private String userId;

    private String oldPassword;

    private String newPassword;

    private String confirmNewPassword;

    public ChangePasswordDto(String userId , String oldPassword , String newPassword , String confirmNewPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
