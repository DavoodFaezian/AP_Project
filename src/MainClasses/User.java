package MainClasses;

import java.util.ArrayList;
import java.util.List;

public class User{

    private List<Account> accounts = new ArrayList<>();

    private String id;

    public User(List<Account> accounts , String id){
        this.accounts = accounts;
        this.id = id;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}