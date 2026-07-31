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

public class CommentRepository {

    private final ConcurrentHashMap<String, ReentrantReadWriteLock> locks = new ConcurrentHashMap<>();
    private static final CommentRepository instance = new CommentRepository();

    private CommentRepository() {
    }

    public static CommentRepository getInstance() {
        return instance;
    }

    private GenericFileManager<Comment> getCommentFileManager(String postId) {
        var lock = locks.computeIfAbsent(postId, k -> new ReentrantReadWriteLock());
        return new GenericFileManager<>(
                "comments" + File.separator + postId + ".txt",
                lock
        );
    }

    public void validateCommentForeignKeys(Comment comment, String postId) {
        if (!UserRepository.getInstance().isUserIdValid(comment.getOwnerId())) {
            throw new ItemNotFoundException("user", comment.getOwnerId());
        }

        if (!PostRepository.getInstance()
                .findPostById(comment.getPostId(),postId)
                .getCommentsAllowed()) {
            throw new CommentNotAllowedException("You cannot comment on this post");
        }
    }

    public void addComment(Comment comment, String postId) {
        comment.validate();
        validateCommentForeignKeys(comment,postId);

        var commentFileManager = getCommentFileManager(postId);
        commentFileManager.addToList(comment);
        commentFileManager.save();
    }

    public void removeComment(Comment comment, String postId) {
        var commentFileManager = getCommentFileManager(postId);
        commentFileManager.removeFromList(comment);
        commentFileManager.save();
    }

    public void removeComment(String id, String postId) {
        Comment remove = findCommentById(id, postId);
        removeComment(remove,postId);
    }

    public Comment findCommentById(String id, String postId) {
        Optional<Comment> comment = getCommentFileManager(postId).findItemById(id);
        if (comment.isEmpty()) {
            throw new ItemNotFoundException("comment", id);
        }
        return comment.get();
    }


    public List<Comment> getAllCommentsByPostId(String postId) {


        return new ArrayList<>(getCommentFileManager(postId).getAll());
    }
}
