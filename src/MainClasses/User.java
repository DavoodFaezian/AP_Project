package MainClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends BaseClass<User>{

    private String userName;

    private String password;

    private List<Photo> photos = new ArrayList<>();

    private List<Album> albums = new ArrayList<>();

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

    public List<Album> getAlbums() {
        return albums;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public void setAlbums(List<Album> albums) {
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