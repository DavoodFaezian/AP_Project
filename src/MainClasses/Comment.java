package MainClasses;

import Exceptions.CommentNotAllowedException;
import Exceptions.FieldIsEmptyException;

public class Comment extends BaseClass<Comment> {

    private String ownerId;

    private String script;

    private String photoId;


    public Comment(String ownerId, String script, String photoId) {
        this.ownerId = ownerId;
        this.script = script;
        this.photoId = photoId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public boolean validateEditComment(String script) {
        if(script.isEmpty()){
           throw new FieldIsEmptyException("Comment should at least contain one character.","script");
        }
        return true;
    }

    public void editComment(String script) {
        validateEditComment(script);
        this.script = script;
    }
}
