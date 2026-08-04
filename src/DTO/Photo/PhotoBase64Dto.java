package DTO.Photo;

public class PhotoBase64Dto {
    private String photoData;

    public PhotoBase64Dto(String photoData) {
        this.photoData = photoData;
    }

    public String getPhotoData() {
        return photoData;
    }
}
