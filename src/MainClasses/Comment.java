package MainClasses;

import Exceptions.CommentNotAllowedException;
import Exceptions.FieldIsEmptyException;

public class Comment extends BaseClass<Comment> {

    private User owner;

    private String script;

    private String id;
    private Photo photo;
    public Comment(User owner, String script, String id,Photo photo) {
        this.owner = owner;
        this.script = script;
        this.id = id;
        this.photo = photo;
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

    @Override
    public void afterLoad() {

    }

    public String getId() {
        return id;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean validateRemoveComment() {
        return true;
    }

    public boolean validateAddComment(){
        if(script.isEmpty()){
            throw new FieldIsEmptyException("Comment should at least contain one character.","script");
        }
        if(!photo.isPermissionForLeavingComment()){
            throw new CommentNotAllowedException("Comments are not allowed.");
        }

        return true;
    }


    public boolean validateEditComment(String script) {
        if(script.isEmpty()){
           throw new FieldIsEmptyException("Comment should at least contain one character.","script");
        }
        return true;
    }
}
