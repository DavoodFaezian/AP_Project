package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class User{

    private Account account;

    private String id;

    private List<Album> albums = new ArrayList<>();

    public User(Account account , String id){
        this.account = account;
        this.id = id;
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

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {

        this.albums = albums;
    }

    public void setId(String id) {
        this.id = id;
    }


}