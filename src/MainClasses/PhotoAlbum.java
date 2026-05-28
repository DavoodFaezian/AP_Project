package MainClasses;

import java.util.Objects;

record PhotoAlbum(Photo photo , Album album){
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PhotoAlbum that = (PhotoAlbum) o;
        return Objects.equals(photo(), that.photo()) && Objects.equals(album(), that.album());
    }

    @Override
    public int hashCode() {
        return Objects.hash(photo(), album());
    }
}