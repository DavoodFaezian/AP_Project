package DTO.Photo;

public class GetAlbumPhotosDto {
    private String sessionId;
    private String albumId;

    public GetAlbumPhotosDto(String sessionId, String albumId) {
        this.sessionId = sessionId;
        this.albumId = albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAlbumId() {
        return albumId;
    }
}
