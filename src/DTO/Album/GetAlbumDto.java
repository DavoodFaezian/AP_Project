package DTO.Album;

public class GetAlbumDto {
    private String sessionId;
    private String albumId;
    private String ownerId;

    public GetAlbumDto(String sessionId, String albumId, String ownerId) {
        this.sessionId = sessionId;
        this.albumId = albumId;
        this.ownerId = ownerId;
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
