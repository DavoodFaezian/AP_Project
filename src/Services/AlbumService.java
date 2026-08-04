package Services;
import DTO.StringResultDto;
import DTO.Album.*;
import Exceptions.ActionFailedException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import Repositories.*;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class AlbumService {
    private static final AlbumService instance = new AlbumService();
    private final AlbumRepository albumRepository = AlbumRepository.getInstance();
     private final PhotoRepository photoRepository = PhotoRepository.getInstance();
     private final PostRepository postRepository = PostRepository.getInstance();

    private AlbumService() {}

    public static AlbumService getInstance() {
        return instance;
    }

    public void validateAlbumName(String albumName) {
        if (albumName.isEmpty()) {
           throw new ActionFailedException("Album name must not be empty.");
        }
    }
    public StringResultDto addAlbum(AddAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String albumName = data.getName();
        Album album = AlbumRepository.getInstance().createAlbum(user.getId() , albumName);
        return new StringResultDto(album.getId());
    }

    public void deleteAlbum(DeleteAlbumDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String albumId = data.getAlbumId();
        Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
        for(String i : album.getPhotoIds()) {
            Photo photo = PhotoRepository.getInstance().findPhotoById(i , user.getId());
            photo.getAlbumIds().remove(album.getId());
            if (photo.getAlbumIds().isEmpty()) {
                PhotoRepository.getInstance().removePhoto(photo);
            } else {
                PhotoRepository.getInstance().update(photo);
            }
        }
        AlbumRepository.getInstance().removeAlbum(album);
    }

    public void editAlbum(EditAlbumDto data) {
        if(data.getAlbumName().isEmpty()){
            throw new ActionFailedException("album name can't be empty");
        }
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        albumRepository.editAlbum(data.getAlbumId(),data.getAlbumName(),user.getId());
    }
    public Set<String> getPhotoIdsOfAlbum(GetAlbumDto data) {
        SessionRepository.getInstance().validateSession(data.getSessionId());
        Album album = albumRepository.findAlbumById(data.getAlbumId(), data.getOwnerId());
        return album.getPhotoIds() != null ? album.getPhotoIds() : Collections.emptySet();
    }
    public AlbumDto getAlbumById(GetAlbumDto data){
        String ownerId = SessionRepository.getInstance().findUserBySessionId(data.getSessionId()).getId();
        if(data.getOwnerId() != null){
            ownerId = data.getOwnerId();
        }
        Album album = albumRepository.findAlbumById(data.getAlbumId(), ownerId);
        return new AlbumDto(album);
    }


    public AlbumListDto getAllAlbumsByOwnerId(GetAllAlbumsDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        List<Album> albums = albumRepository.getAlbumsByOwner(user.getId());
        if (albums == null) {
            return new AlbumListDto(Collections.emptyList());
        }

        return new AlbumListDto(albums.stream().map(AlbumDto::new).collect(Collectors.toList()));
    }


    public AlbumDto getAlbum(GetAlbumDto data) {
        SessionRepository.getInstance().validateSession(data.getSessionId());
        return new AlbumDto(albumRepository.findAlbumById(data.getAlbumId(),data.getOwnerId()));
    }
}

