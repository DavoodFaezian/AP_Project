package MainClasses;
public class Comment {

    private Account ownerAccount;

    private String script;

    private String id;

    public Comment(Account ownerAccount, String script, String id) {
        this.ownerAccount = ownerAccount;
        this.script = script;
        this.id = id;
    }

    public Account getOwnerAccount() {
        return ownerAccount;
    }

    public String getScript() {
        return script;
    }

    public String getId() {
        return id;
    }

    public void setOwnerAccount(Account ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setId(String id) {
        this.id = id;
    }
}
