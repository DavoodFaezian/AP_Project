package MainClasses;

import Exceptions.FieldIsEmptyException;
import Exceptions.PasswordContainsUserNameException;
import Exceptions.PasswordNotLongEnoughException;
import Exceptions.PasswordNotStrongException;

import java.util.*;
import java.util.regex.Pattern;

public class User extends BaseClass<User>{

    private String userName;

    private String password;

    private String profilePhotoId;

    private Set<String> photoIds = new HashSet<>();

    private Set<String> albumIds = new HashSet<>();

    private Set<String> sharedPhotoIds = new HashSet<>();

    private Theme theme = Theme.LIGHT;

    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    public void addPhotoToSharedPhotos(String photoId){
        sharedPhotoIds.add(photoId);
    }

    public void removePhotoFromSharedPhotos(String photoId){
        sharedPhotoIds.remove(photoId);
    }

    public void addPhotoToPhotos(String photoId){
        photoIds.add(photoId);
    }

    public void removePhotoFromPhotos(String photoId){
        photoIds.remove(photoId);
    }

    public void addAlbumToAlbums(String albumId){
        albumIds.add(albumId);
    }

    public void removeAlbumFromAlbums(String albumId){
        albumIds.remove(albumId);
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public void setProfilePhotoId(String profilePhotoId) {
        this.profilePhotoId = profilePhotoId;
    }

    public String getProfilePhotoId() {
        return profilePhotoId;
    }

    public Theme getTheme(){
        return theme;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getPhotoIds() {
        return photoIds;
    }

    public void setPhotoIds(Set<String> photoIds) {
        this.photoIds = photoIds;
    }

    public Set<String> getAlbumIds() {
        return albumIds;
    }

    public void setAlbumIds(Set<String> albumIds) {
        this.albumIds = albumIds;
    }

    public Set<String> getSharedPhotoIds() {
        return sharedPhotoIds;
    }

    public void setSharedPhotoIds(Set<String> sharedPhotoIds) {
        this.sharedPhotoIds = sharedPhotoIds;
    }

    public void setTheme(Theme theme){
        this.theme = theme;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getId() , user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, password);
    }


    public void validateRemoveUser() {
    }
}