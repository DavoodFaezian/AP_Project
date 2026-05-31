package MainClasses;
public class Comment extends BaseClass<Comment> {

    private User owner;

    private String script;

    public Comment(User owner, String script) {
        this.owner = owner;
        this.script = script;
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

    public void setScript(String script) {
        this.script = script;
    }

}
