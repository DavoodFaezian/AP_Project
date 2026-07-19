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

    private GenericFileManager<Comment> getCommentFileManager(String photoId) {
        var lock = locks.computeIfAbsent(photoId, k -> new ReentrantReadWriteLock());
        return new GenericFileManager<>(
                "comments" + File.separator + photoId + ".txt",
                lock
        );
    }

    public void validateCommentForeignKeys(Comment comment, String photoOwnerId) {
        if (!UserRepository.getInstance().isUserIdValid(comment.getOwnerId())) {
            throw new ItemNotFoundException("user", comment.getOwnerId());
        }

        if (!PhotoRepository.getInstance()
                .findPhotoById(comment.getPhotoId(),photoOwnerId)
                .isPermissionForLeavingComment()) {
            throw new CommentNotAllowedException("You cannot comment on this photo");
        }
    }

    public void addComment(Comment comment) {
        comment.validate();
        validateCommentForeignKeys(comment);

        var commentFileManager = getCommentFileManager(comment.getPhotoId());
        commentFileManager.addToList(comment);
        commentFileManager.save();
    }

    public void removeComment(Comment comment) {
        var commentFileManager = getCommentFileManager(comment.getPhotoId());
        commentFileManager.removeFromList(comment);
        commentFileManager.save();
    }

    public void removeComment(String id, String photoId) {
        Comment remove = findCommentById(id, photoId);
        removeComment(remove);
    }

    public Comment findCommentById(String id, String photoId) {
        Optional<Comment> comment = getCommentFileManager(photoId).findItemById(id);
        if (comment.isEmpty()) {
            throw new ItemNotFoundException("comment", id);
        }
        return comment.get();
    }

    public List<Comment> getCommentsByPhotoId(String photoId) {
        return getCommentFileManager(photoId).getAll();
    }

    public List<Comment> getCommentsByOwner(String ownerId, List<String> photoIds) {
        List<Comment> result = new ArrayList<>();

        for (String photoId : photoIds) {
            List<Comment> comments = getCommentFileManager(photoId)
                    .filterItems(comment -> comment.getOwnerId().equals(ownerId));
            result.addAll(comments);
        }

        return result;
    }

    public List<Comment> getAllComments(List<String> photoIds) {
        List<Comment> result = new ArrayList<>();

        for (String photoId : photoIds) {
            result.addAll(getCommentFileManager(photoId).getAll());
        }

        return result;
    }
}
