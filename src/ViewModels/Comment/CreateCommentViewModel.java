package ViewModels.Comment;

public class CreateCommentViewModel {
    private String photoId;
    private String script;

    public CreateCommentViewModel(String photoId, String script) {
        this.photoId = photoId;
        this.script = script;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
