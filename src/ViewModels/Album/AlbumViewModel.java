package ViewModels.Album;


import ViewModels.Photo.PhotoViewModel;

import java.util.HashSet;
import java.util.Set;

public class AlbumViewModel {
    private String albumName;
    private Set<PhotoViewModel> photos = new HashSet<>();

}
