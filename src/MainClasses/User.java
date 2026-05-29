package MainClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User{

    private String userName;

    private String password;

    private Account account;

    private String id;

    private List<Photo> photos = new ArrayList<>();

    private List<Album> albums = new ArrayList<>();

    public User(String userName, String password, Account account , String id){
        this.userName = userName;
        this.password = password;
        this.account = account;
        this.id = id;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getId() {
        return id;
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

    public void setId(String id) {
        this.id = id;
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