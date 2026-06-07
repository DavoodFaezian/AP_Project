package MainClasses;

import Repositories.PhotoRepository;
import Repositories.UserRepository;

public class PhotoSharedUser extends BaseClass<PhotoSharedUser>{
    private String photoId;
    private String sharedUserId;

    public PhotoSharedUser(String photoId, String sharedUserId) {
        this.photoId = photoId;
        this.sharedUserId = sharedUserId;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getSharedUserId() {
        return sharedUserId;
    }

    public void setSharedUserId(String sharedUserId) {
        this.sharedUserId = sharedUserId;
    }


}
