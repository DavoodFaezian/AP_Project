package ViewModels.Album;

import MainClasses.User;

public class CreateAlbumViewModel {
    private String albumName;
    public CreateAlbumViewModel(String albumName, User user) {
        this.albumName = albumName;

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
