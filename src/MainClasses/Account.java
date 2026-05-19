package MainClasses;

public class Account {

    private String password;

    private String userName;

    private String id;

    public Account(String password , String userName , String id){
        this.password = password;
        this.userName = userName;
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
