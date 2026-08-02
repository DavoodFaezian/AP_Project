package DTO.Album;

public class GetAllAlbumsDto {
    private String sessionId;

    public GetAllAlbumsDto(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
