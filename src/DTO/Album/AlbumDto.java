package DTO.Album;

import MainClasses.Album;

import java.util.Objects;
import java.util.Set;

public class AlbumDto {
    private final String ownerId;
    private final String albumName;
    private final Set<String> photoIds;
    private final Set<String> postIds;

    public AlbumDto(Album album) {
        this.ownerId = album.getOwnerId();
        this.albumName = album.getAlbumName();
        this.photoIds = album.getPhotoIds();
        this.postIds = album.getPostIds();
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

    public Set<String> getPostIds() {
        return postIds;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AlbumDto) obj;
        return Objects.equals(this.ownerId, that.ownerId) &&
                Objects.equals(this.albumName, that.albumName) &&
                Objects.equals(this.photoIds, that.photoIds) &&
                Objects.equals(this.postIds, that.postIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerId, albumName, photoIds, postIds);
    }

    @Override
    public String toString() {
        return "AlbumDto[" +
                "ownerId=" + ownerId + ", " +
                "albumName=" + albumName + ", " +
                "photoIds=" + photoIds + ", " +
                "postIds=" + postIds + ']';
    }
}