package Services;

import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.PostRepository;

import java.util.Collections;
import java.util.Set;
import java.util.List;

public class AlbumService {
    private static final AlbumService instance = new AlbumService();
    private final AlbumRepository albumRepository = AlbumRepository.getInstance();
     private final PhotoRepository photoRepository = PhotoRepository.getInstance();
     private final PostRepository postRepository = PostRepository.getInstance();

    private AlbumService() {}

    public static AlbumService getInstance() {
        return instance;
    }

    public void addAlbum(Album album) {
        albumRepository.addAlbum(album);
    }

    public void editAlbum(Album album) {
        album.updateTime();
        albumRepository.editAlbum(album);
    }

    public void deleteAlbum(String id, String ownerId) {
        Album album = albumRepository.findAlbumById(id, ownerId);

        if (album.getPhotoIds() != null) {
            for (String photoId : album.getPhotoIds()) {
                 Photo photo = photoRepository.findPhotoById(photoId, ownerId);
                 photo.getPhotoAlbumIds().remove(id);
                 photoRepository.editPhoto(photo);
            }
        }

        if (album.getPostIds() != null) {
            for (String postId : album.getPostIds()) {
                 Post post = postRepository.findPostById(postId, ownerId);
                 post.getAlbumIds().remove(id);
                 postRepository.editPost(post);
            }
        }

        albumRepository.removeAlbum(album);
    }

    public Set<String> getPhotoIdsOfAlbum(String albumId, String ownerId) {
        Album album = albumRepository.findAlbumById(albumId, ownerId);
        return album.getPhotoIds() != null ? album.getPhotoIds() : Collections.emptySet();
    }

    public Set<String> getPostIdsOfAlbum(String albumId, String ownerId) {
        Album album = albumRepository.findAlbumById(albumId, ownerId);
        return album.getPostIds() != null ? album.getPostIds() : Collections.emptySet();
    }
    private AlbumDto mapAlbumToDto(Album album) {
        if (album == null) {
            return null;
        }
        return new AlbumDto(
                album.getOwnerId(),
                album.getAlbumName(),
                album.getPhotoIds(),
                album.getPostIds()
        );
    }

    public List<AlbumDto> getAllAlbumsByOwnerId(String ownerId) {
        List<Album> albums = albumRepository.getAlbumsByOwner(ownerId);
        if (albums == null) {
            return Collections.emptyList();
        }
        List<AlbumDto> dtos = new java.util.ArrayList<>();
        for (Album album : albums) {
            dtos.add(mapAlbumToDto(album));
        }
        return dtos;
    }
}