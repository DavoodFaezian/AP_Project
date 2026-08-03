package DTO.Album;

import MainClasses.Album;

import java.util.Set;

public class EditAlbumDto {

    private String sessionId;
    private String albumId;
    private String albumName;

    public EditAlbumDto(String sessionId, String albumName,String albumId) {
        this.sessionId = sessionId;
        this.albumName = albumName;
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

    public String getAlbumName() {
        return albumName;
    }
}
