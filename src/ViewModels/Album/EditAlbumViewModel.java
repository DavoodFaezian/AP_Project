package ViewModels.Album;

public class EditAlbumViewModel {
    private String id;
    private String albumName;

    public EditAlbumViewModel(String id, String albumName) {
        this.id = id;
        this.albumName = albumName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
