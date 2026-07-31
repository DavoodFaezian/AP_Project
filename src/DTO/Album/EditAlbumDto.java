package DTO.Album;

import java.util.Set;

public class EditAlbumDto {
    private String albumId;
    private String sessionId;
    private String albumName;

    public EditAlbumDto(String albumId, String ownerId, String albumName) {
        this.albumId = albumId;
        this.sessionId = ownerId;
        this.albumName = albumName;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getAlbumName() {
        return albumName;
    }
}
