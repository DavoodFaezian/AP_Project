package DTO.Album;

public class GetAlbumItemsDto {
    private String albumId;
    private String sessionId;

    public GetAlbumItemsDto(String albumId, String ownerId) {
        this.albumId = albumId;
        this.sessionId = ownerId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
