package DTO.Album;

import java.util.Objects;
import java.util.Set;

public class AlbumDto {
    private final String ownerId;
    private final String albumName;
    private final Set<String> photoIds;
    private final Set<String> postIds;

    public AlbumDto(String ownerId, String albumName, Set<String> photoIds, Set<String> postIds) {
        this.ownerId = ownerId;
        this.albumName = albumName;
        this.photoIds = photoIds;
        this.postIds = postIds;
    }

    public String ownerId() {
        return ownerId;
    }

    public String albumName() {
        return albumName;
    }

    public Set<String> photoIds() {
        return photoIds;
    }

    public Set<String> postIds() {
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