package MainClasses;
public class Comment {

    private String script;

    private String id;

    public Comment(Account ownerAccount, String script, String id) {
        this.script = script;
        this.id = id;
    }

    public String getScript() {
        return script;
    }

    public String getId() {
        return id;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setId(String id) {
        this.id = id;
    }
}
