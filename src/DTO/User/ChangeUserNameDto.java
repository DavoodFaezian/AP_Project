package DTO.User;

public class ChangeUserNameDto {
    private String sessionId;
    private String newUserName;

    public ChangeUserNameDto(String sessionId, String newUserName) {
        this.sessionId = sessionId;
        this.newUserName = newUserName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getNewUserName() {
        return newUserName;
    }
}
