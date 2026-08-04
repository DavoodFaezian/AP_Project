package DTO.Album;

import MainClasses.Album;

import java.util.Objects;
import java.util.Set;

public class AlbumDto {
    private final String id;
    private final String albumName;
    private final String ownerId;
    private final Set<String> photoIds;

    public AlbumDto(Album album) {
        this.id = album.getId();
        this.albumName = album.getAlbumName();
        this.ownerId = album.getOwnerId();
        this.photoIds = album.getPhotoIds();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AlbumDto) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.albumName, that.albumName) &&
                Objects.equals(this.photoIds, that.photoIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, albumName, photoIds);
    }

    @Override
    public String toString() {
        return "AlbumDto[" +
                "id=" + id + ", " +
                "albumName=" + albumName + ", " +
                "photoIds=" + photoIds + ']';
    }
}