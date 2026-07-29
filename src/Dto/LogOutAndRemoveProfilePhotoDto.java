package Dto;

public class LogOutAndRemoveProfilePhotoDto {

    private String sessionId;

    public LogOutAndRemoveProfilePhotoDto(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
