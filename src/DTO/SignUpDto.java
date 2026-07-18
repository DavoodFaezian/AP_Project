package DTO;

import com.google.gson.JsonObject;

public class SignUpDto {

    private String userName;

    private String password;

    private String repeatedPassword;

    public SignUpDto(String userName , String password , String repeatedPassword) {
        this.userName = userName;
        this.password = password;
        this.repeatedPassword = repeatedPassword;
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
