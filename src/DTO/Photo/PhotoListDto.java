package DTO.Photo;

import java.util.List;

public class PhotoListDto {
    List<PhotoDto> photos;

    public PhotoListDto(List<PhotoDto> photos) {
        this.photos = photos;
    }

    public List<PhotoDto> getPhotos() {
        return photos;
    }
}
