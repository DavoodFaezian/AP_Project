package DTO.Album;

import java.util.List;

public class AlbumListDto {
    List<AlbumDto> albums;

    public AlbumListDto(List<AlbumDto> albums) {
        this.albums = albums;
    }

    public List<AlbumDto> getAlbums() {
        return albums;
    }
}
