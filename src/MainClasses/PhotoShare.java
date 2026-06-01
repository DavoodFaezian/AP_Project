package MainClasses;

public class PhotoShare{

    private void validateUser(User user){
        if(user == null){
            throw new NullPointerException("User is null!!!");
        }
    }

    private void validatePhoto(Photo photo){
        if(photo == null){
            throw new NullPointerException("Photo is null!!!");
        }
    }

    public void sharePhoto(Photo photo , User user){
        validateUser(user);
        validatePhoto(photo);
        photo.getSharedWithUsers().add(user);
        user.getPhotos().add(photo);
        photo.updateDateOfShare();
    }
}
