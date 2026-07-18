package DTO;

import com.google.gson.JsonObject;

public class LogInDto {

    private String userId;

    private String userName;

    private String password;

    private String repeatedPassword;

    public LogInDto(String userId , String userName , String password , String repeatedPassword) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}