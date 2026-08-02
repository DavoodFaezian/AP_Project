package MainClasses;

import java.util.*;

public class User extends BaseClass<User>{

    private String userName;

    private String password;


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


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
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


}