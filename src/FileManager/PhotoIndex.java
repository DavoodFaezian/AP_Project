package FileManager;

public class PhotoIndex {

    private final String photoId;

    private final long begin;

    private final int length;

    public PhotoIndex(String photoId, long begin, int length) {
        this.photoId = photoId;
        this.begin = begin;
        this.length = length;
    }

    public String getPhotoId() {
        return photoId;
    }

    public long getBegin() {
        return begin;
    }

    public int getLength() {
        return length;
    }
}
