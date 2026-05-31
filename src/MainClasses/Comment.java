package MainClasses;
public class Comment extends BaseClass<Comment> {

    private User owner;

    private String script;

    private String id;

    public Comment(User owner, String script, String id) {
        this.owner = owner;
        this.script = script;
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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
