package DTO.Album;

import MainClasses.Album;

import java.util.Set;

public class EditAlbumDto {

    private String sessionId;

    private Album album;

    public EditAlbumDto(String sessionId, Album album) {
        this.sessionId = sessionId;
        this.album = album;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Album getAlbumName() {
        return album;
    }

    public void setAlbumName(Album album) {
        this.album = album;
    }
}
