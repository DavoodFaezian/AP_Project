package Repositories;

import Exceptions.CommentNotAllowedException;
import Exceptions.ItemNotFoundException;
import FileManager.GenericFileManager;
import MainClasses.Comment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CommentRepository extends BaseRepository<Comment> {

    private static final CommentRepository instance = new CommentRepository();

    private CommentRepository() {
        super("comments");
    }

    public static CommentRepository getInstance() {
        return instance;
    }


    public void validateCommentForeignKeys(Comment comment, String postOwnerId) {
        if (!UserRepository.getInstance().isUserIdValid(comment.getOwnerId())) {
            throw new ItemNotFoundException("user", comment.getOwnerId());
        }

        if (!PostRepository.getInstance()
                .findPostById(comment.getPostId(),postOwnerId)
                .getCommentsAllowed()) {
            throw new CommentNotAllowedException("You cannot comment on this post");
        }
    }

    public void addComment(Comment comment,String postOwnerId) {
        comment.validate();
        validateCommentForeignKeys(comment,postOwnerId);

        var commentFileManager = getFileManager(comment.getPostId());
        commentFileManager.addToList(comment);
        commentFileManager.save();
    }

    public void removeComment(Comment comment) {
        var commentFileManager = getFileManager(comment.getPostId());
        commentFileManager.removeFromList(comment);
        commentFileManager.save();
    }

    public void removeComment(String id, String postId) {
        Comment remove = findCommentById(id, postId);
        removeComment(remove);
    }

    public Comment findCommentById(String id, String postOwnerId) {
        Optional<Comment> comment = getFileManager(postOwnerId).findItemById(id);
        if (comment.isEmpty()) {
            throw new ItemNotFoundException("comment", id);
        }
        return comment.get();
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return getFileManager(postId).getAll();
    }

    public List<Comment> getCommentsByOwner(String ownerId, List<String> postIds) {
        List<Comment> result = new ArrayList<>();

        for (String postId : postIds) {
            List<Comment> comments = getFileManager(postId)
                    .filterItems(comment -> comment.getOwnerId().equals(ownerId));
            result.addAll(comments);
        }

        return result;
    }

    public List<Comment> getAllComments(List<String> postIds) {
        List<Comment> result = new ArrayList<>();

        for (String postId : postIds) {
            result.addAll(getFileManager(postId).getAll());
        }

        return result;
    }
}
