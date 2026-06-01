package MainClasses;

import Exceptions.FieldIsEmptyException;
import Exceptions.PasswordContainsUserNameException;
import Exceptions.PasswordNotLongEnoughException;
import Exceptions.PasswordNotStrongException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User extends BaseClass<User>{

    private String userName;

    private String password;

    private Set<Photo> photos = new TreeSet<>(Comparator.comparing(Photo::getCreatedAt));

    private Set<Album> albums = new TreeSet<>(Comparator.comparing(Album::getCreatedAt));

    private static final int MIN_LENGTH = 8;

    private void validateUserName(String userName){
        if(userName.isEmpty()){
            throw new FieldIsEmptyException("User name must not be empty!!!" , this.userName);
        }
    }

    private void validatePassword(String password){
        if(password.isEmpty()){
            throw new FieldIsEmptyException("Password must not be empty!!!" , this.password);
        }
    }

    private void validateLength(String password){
        if(password.length() < MIN_LENGTH){
            throw new PasswordNotLongEnoughException("Password must have at least 8 characters!!!");
        }
    }

    private void validateStrength(String password){
        if(!Pattern.compile("[!@#$%^&*+=_?]").matcher(password).find()){
            throw new PasswordNotStrongException("Password must contain at least one special character!!!");
        }
    }

    private void validateDoesNotContainUserName(String password){
        if(password.contains(userName)){
            throw new PasswordContainsUserNameException("Password must not contain user name!!!");
        }
    }

    public User(String userName, String password){
        validateUserName(userName);
        this.userName = userName;
        validatePassword(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(password);
        this.password = password;
    }

    public String getUserName(){
        return userName;
    }

    public String getPassword(){
        return password;
    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setUserName(String userName) {
        validateUserName(userName);
        this.userName = userName;
    }

    public void setPassword(String password) {
        validatePassword(password);
        validateLength(password);
        validateStrength(password);
        validateDoesNotContainUserName(password);
        this.password = password;
    }

    public Set<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<Photo> photos) {
        this.photos = photos;
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserName(), user.getUserName()) && Objects.equals(getId() , user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserName());
    }
}