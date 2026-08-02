package DTO.Photo;

public class GetAllPhotosDto {
    private String sessionId;

    public GetAllPhotosDto(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
