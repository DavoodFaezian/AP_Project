package Services;
import Annotaions.ServiceAction;
import DTO.Photo.*;
import DTO.StringResultDto;
import DTO.SearchDto;
import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.User;
import MainClasses.UserProfile;
import Repositories.*;

import java.util.Base64;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PhotoService {

    private final static PhotoService instance = new PhotoService();

    private PhotoService(){}

    @ServiceAction
    public static PhotoService getInstance() {
        return instance;
    }

    private void validatePhotoName(String photoName) {
        if (photoName.isEmpty()) {
           throw new ActionFailedException("Photo name must not be empty.");
        }
    }

    @ServiceAction
    public StringResultDto addPhoto(AddPhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        String photoName = data.getPhotoName();
        String title = data.getTitle();
        Set<String> albumIds = data.getAlbumIds();
        Set<String> tags = data.getTags();
        String caption = data.getCaption();
        Boolean isFavorable = data.getFavorable();
        validatePhotoName(photoName);
        Photo photo = PhotoRepository.getInstance().createPhoto(user.getId(),title , photoName , albumIds , tags , caption , isFavorable);
        for(var albumId : albumIds){
            if (!albumId.isEmpty()) {
                Album album = AlbumRepository.getInstance().findAlbumById(albumId , user.getId());
                album.getPhotoIds().add(photo.getId());
                AlbumRepository.getInstance().update(album);
            }
        }
        return new StringResultDto(photo.getId());
    }

    @ServiceAction
    public void deletePhoto(DeletePhotoDto data) {
        String sessionId = data.getSessionId();
        String photoId = data.getPhotoId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        Photo photo = PhotoRepository.getInstance().findPhotoById(photoId , user.getId());
        for (String i : photo.getAlbumIds()) {
            if (!i.isEmpty()) {
                Album album = AlbumRepository.getInstance().findAlbumById(i , user.getId());
                album.getPhotoIds().remove(photoId);
                album.updateTime();
                AlbumRepository.getInstance().update(album);
            }
        }
        PhotoRepository.getInstance().removePhoto(photo);

    }

    @ServiceAction
    public void editPhoto(EditPhotoDto data) {
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        PhotoDto photo = data.getPhoto();
        PhotoAlbumService.getInstance().editPhotoByAlbum(
                new EditPhotoByAlbumDto(
                        sessionId,
                        data.getPhoto().getId(),
                        data.getPhoto().getAlbumIds()
                )
        );
        Photo edit = PhotoRepository.getInstance().findPhotoById(data.getPhoto().getId(),user.getId());
        edit.setPhotoName(photo.getPhotoName());
        edit.setAlbumIds(photo.getAlbumIds());
        edit.setCaption(photo.getCaption());
        edit.setTags(photo.getTags());
        edit.setFavorable(photo.getFavorable());
        edit.setTitle(photo.getTitle());

        PhotoRepository.getInstance().editPhoto(edit);
    }

    @ServiceAction
    public StringResultDto uploadPhoto(UploadPhotoDto data ) {
        byte[] bytes = Base64.getDecoder().decode(data.getPhotoData());
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());
        if(data.isProfilePicture()){
            Optional<UserProfile> profile = UserProfileRepository.getInstance().getUserProfileByUserId(user.getId());
            if(profile.isEmpty()){
                throw new ActionFailedException("Ops! profile was not found.");
            }
            UserProfile notNullProfile = profile.get();
            String photoName = PhotoRepository.getInstance().uploadPhoto(user.getId(),bytes);
            notNullProfile.setProfilePhotoName(photoName);
            UserProfileRepository.getInstance().updateUserProfile(notNullProfile);
            return new StringResultDto(photoName);
        }
        return new StringResultDto(PhotoRepository.getInstance().uploadPhoto(user.getId(),bytes));
    }

    @ServiceAction
    public PhotoDto getPhotoById(GetPhotoDto data){
        String sessionId = data.getSessionId();
        String ownerId = SessionRepository.getInstance().findUserBySessionId(sessionId).getId();
        if(data.getOwnerId() != null){
            ownerId = data.getOwnerId();
        }
        var res = PhotoRepository.getInstance().getPhotoById(ownerId, data.getPhotoId());
        if(res.isEmpty())
        {
            throw new ItemNotFoundException("Photo", data.getPhotoId());
        }
        return new PhotoDto(res.get());

    }

    @ServiceAction
    public PhotoBase64Dto getPhotoBytes(GetPhotoDto data){
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        if(data.getOwnerId() == null){
            return new PhotoBase64Dto(PhotoRepository.getInstance().getPhotoData(user.getId(), data.getPhotoId()));
        }
        return new PhotoBase64Dto(PhotoRepository.getInstance().getPhotoData(data.getOwnerId(), data.getPhotoId()));


    }

    @ServiceAction
    public PhotoListDto getPhotosByOwnerId(GetAllPhotosDto data){
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        return new PhotoListDto(PhotoRepository.getInstance().getPhotosByOwnerId(user.getId())
                .stream()
                .map(PhotoDto::new)
                .collect(Collectors.toList()));


    }
    @ServiceAction
    public PhotoListDto searchPhotos(SearchDto data){
        String sessionId = data.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        if(data.getQuery() == null){
            return getPhotosByOwnerId(new GetAllPhotosDto(sessionId));
        }
        return new PhotoListDto(PhotoRepository.getInstance().filterPhotos(data.getQuery(),user.getId())
                .stream()
                .map(PhotoDto::new)
                .collect(Collectors.toList()));


    }
}
