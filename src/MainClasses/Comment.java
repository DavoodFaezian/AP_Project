package MainClasses;

import Exceptions.CommentNotAllowedException;
import Exceptions.FieldIsEmptyException;
import Exceptions.ItemNotFoundExeption;

public class Comment extends BaseClass<Comment> {
    private Photo photo;
    private User owner;

    private String script;

    private String id;

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

    public String getId() {
        return id;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public boolean validateRemoveComment() {
        if(!photo.getComments().contains(this)){
            throw new RuntimeException("Photo with id of "+photo.getId()
            +" doesn't have a comment with id of "+getId());
        }
        return true;
    }
    public boolean validateAddComment() {
        if(!photo.isPermissionForLeavingComment()){
            throw new CommentNotAllowedException("Comments are not allowed on photo with the id of"+photo.getId());
        }
        if(script.isEmpty()){
            throw new FieldIsEmptyException("Comment should at least contain a character.","script");
        }
        return true;
    }
    public boolean validateEditComment(String script){
        if(script.isEmpty()){
            throw new FieldIsEmptyException("Comment should at least contain a character.","script");
        }
        return true;
    }
}
