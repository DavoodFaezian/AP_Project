package DTO.Album;

import java.util.Objects;

public class DeleteAlbumDto {
    private String albumId;
    private String sessionId;

    public DeleteAlbumDto(String albumId, String sessionId) {
        this.albumId = albumId;
        this.sessionId = sessionId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DeleteAlbumDto) obj;
        return Objects.equals(this.albumId, that.albumId) &&
                Objects.equals(this.sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumId, sessionId);
    }

    @Override
    public String toString() {
        return "DeleteAlbumDto[" +
                "albumId=" + albumId + ", " +
                "sessionId=" + sessionId + ']';
    }

}
