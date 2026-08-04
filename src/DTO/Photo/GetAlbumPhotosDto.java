package DTO.Photo;

public class GetAlbumPhotosDto {
    private String sessionId;
    private String ownerId;
    private String albumId;

    public GetAlbumPhotosDto(String sessionId, String ownerId, String albumId) {
        this.sessionId = sessionId;
        this.ownerId = ownerId;
        this.albumId = albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getOwnerId() {
        return ownerId;
    }
}
