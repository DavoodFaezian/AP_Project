package Repositories;

import Exceptions.ItemNotFoundException;
import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Comment;

import java.util.List;
import java.util.Optional;

public class CommentRepository {

    private final GenericFileManager<Comment> commentFileManager;
    private static final CommentRepository instance = new CommentRepository();

    private CommentRepository(){
        this.commentFileManager = new GenericFileManager<>("comment.txt");
    }

    public static CommentRepository getInstance(){
        return instance;
    }

    public void addComment(Comment comment){
        commentFileManager.addToList(comment);
        commentFileManager.save();
    }

    public void removeComment(Comment comment){
       commentFileManager.removeFromList(comment);
       commentFileManager.save();
    }

    public void removeComment(String id){
        Comment remove = findCommentById(id);
        removeComment(remove);
    }


    public Comment findCommentById(String id){
        Optional<Comment> comment = commentFileManager.findItemById(id);
        if(comment.isEmpty()){
            throw new ItemNotFoundException("comment",id);
        }
        return comment.get();
    }

    public List<Comment> getCommentsByPhotoId(String photoId){
        return commentFileManager.filterItems((comment)->comment.getPhotoId().equals(photoId));
    }

    public List<Comment> getCommentsByOwner(String ownerId){
        return commentFileManager.filterItems((comment)->comment.getOwnerId().equals(ownerId));
    }

}
