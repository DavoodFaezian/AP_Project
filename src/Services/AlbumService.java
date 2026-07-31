package Services;

import DTO.Album.AddAlbumDto;
import DTO.Album.AlbumDto;
import DTO.Album.DeleteAlbumDto;
import DTO.Album.EditAlbumDto;
import DTO.Album.GetAlbumItemsDto;
import DTO.Album.GetAlbumsByOwnerDto;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.PhotoRepository;
import Repositories.PostRepository;
import Repositories.SessionRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlbumService {
    private static final AlbumService instance = new AlbumService();
    private final AlbumRepository albumRepository = AlbumRepository.getInstance();
    private final PhotoRepository photoRepository = PhotoRepository.getInstance();
    private final PostRepository postRepository = PostRepository.getInstance();

    private AlbumService() {}

    public static AlbumService getInstance() {
        return instance;
    }

    public void addAlbum(AddAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String albumName = data.getName();
        albumRepository.createAlbum(user.getId(), albumName);
    }

    public void editAlbum(EditAlbumDto data) {

        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Album album = albumRepository.findAlbumById(data.getAlbumId(), ownerId);
        album.setAlbumName(data.getAlbumName());
        album.updateTime();

        albumRepository.editAlbum(album);
    }


    public void deleteAlbum(DeleteAlbumDto data) {

        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Album album = albumRepository.findAlbumById(data.getAlbumId(), ownerId);

        if (album.getPhotoIds() != null) {
            for (String photoId : album.getPhotoIds()) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPhotoAlbumIds() != null) {
                    photo.getPhotoAlbumIds().remove(album.getId());
                }

                photoRepository.editPhoto(photo);
            }
        }

        if (album.getPostIds() != null) {
            for (String postId : album.getPostIds()) {

                Post post = postRepository.findPostById(postId, ownerId);

                if (post.getAlbumIds() != null) {
                    post.getAlbumIds().remove(album.getId());
                }

                postRepository.editPost(post);
            }
        }

        albumRepository.removeAlbum(album);
    }


    public Set<String> getPhotoIdsOfAlbum(GetAlbumItemsDto data) {

        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        Album album = albumRepository.findAlbumById(data.getAlbumId(), user.getId());

        return album.getPhotoIds() != null
                ? album.getPhotoIds()
                : Collections.emptySet();
    }

    public Set<String> getPostIdsOfAlbum(GetAlbumItemsDto data) {

        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        Album album = albumRepository.findAlbumById(data.getAlbumId(), user.getId());

        return album.getPostIds() != null
                ? album.getPostIds()
                : Collections.emptySet();
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
    }public List<AlbumDto> getAllAlbumsByOwnerId(GetAlbumsByOwnerDto data) {

        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        List<Album> albums = albumRepository.getAlbumsByOwner(user.getId());

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
