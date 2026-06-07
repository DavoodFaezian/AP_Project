package MainClasses;
public class Admin extends BaseClass<Admin> {

    private String adminName;

    private String password;

    private String id;

    public Admin(String adminName , String password , String id){
        this.adminName = adminName;
        this.password = password;
        this.id = id;
    }

    public String getAdminName(){
        return adminName;
    }

    public void setAdminName(String adminName){
        this.adminName = adminName;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
