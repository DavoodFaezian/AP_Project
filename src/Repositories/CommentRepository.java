package Repositories;

import Exceptions.ItemNotFoundException;
import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Comment;

import java.util.List;
import java.util.Optional;

public class CommentRepository {
    private GenericFileManager<Comment> commentFileManager;
    private CommentRepository(){
        this.commentFileManager = new GenericFileManager<>(Comment.fileName);
    }
    public static CommentRepository getInstance(){
        return new CommentRepository();
    }
    public void addComment(Comment comment){
        if(comment.validateAddComment()){
            commentFileManager.addToList(comment);
        }
    }
    public void removeComment(Comment comment){
        if(comment.validateRemoveComment()){
            commentFileManager.removeFromList(comment);
        }
    }
    public void removeComment(String id){
        Comment remove = findCommentById(id);
        removeComment(remove);
    }
    public void editComment(String id,String script){
        Optional<Comment> edit = commentFileManager.findItemById(id);
        if(edit.get().validateEditComment(script)){
            edit.get().setScript(script);
        }
    }
    public Comment findCommentById(String id){
        Optional<Comment> comment = commentFileManager.findItemById(id);
        if(comment.isEmpty()){
            throw new ItemNotFoundException("comment",id);
        }
        return comment.get();
    }
    public List<Comment> getCommentsByPhotoId(String photoId){
        return commentFileManager.filterItems((comment)->comment.getPhoto().getId().equals(photoId));
    }

    public List<Comment> getCommentsByOwnered(String ownerId){
        return commentFileManager.filterItems((comment)->comment.getOwner().getId().equals(ownerId));
    }


}
