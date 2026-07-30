package Dto;

public class DeleteAlbumDto {

    private String sessionId;

    private String albumId;

    public DeleteAlbumDto(String sessionId, String albumId) {
        this.sessionId = sessionId;
        this.albumId = albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
