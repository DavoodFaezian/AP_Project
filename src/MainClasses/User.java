package MainClasses;

import java.util.*;

public class User extends BaseClass<User>{

    private String userName;

    private String password;

    private Set<Photo> photos = new TreeSet<>(Comparator.comparing(Photo::getCreatedAt));

    private Set<Album> albums = new TreeSet<>(Comparator.comparing(Album::getCreatedAt));

    public User(String userName, String password){
        this.userName = userName;
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
        this.userName = userName;
    }

    public void setPassword(String password) {
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
        return Objects.equals(getUserName(), user.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUserName());
    }
}