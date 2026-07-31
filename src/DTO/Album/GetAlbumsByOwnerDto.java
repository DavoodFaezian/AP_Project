package DTO.Album;

public class GetAlbumsByOwnerDto {
    private String ownerId;
    private String sessionId;

    public GetAlbumsByOwnerDto(String ownerId,String sessionId) {
        this.ownerId = ownerId;
        this.sessionId = sessionId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
