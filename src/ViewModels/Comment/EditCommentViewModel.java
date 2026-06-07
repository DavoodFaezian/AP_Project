package ViewModels.Comment;

public class EditCommentViewModel {
    private String id;
    private String script;

    public EditCommentViewModel(String id, String script) {
        this.id = id;
        this.script = script;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
