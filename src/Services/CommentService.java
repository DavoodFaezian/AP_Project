package Services;

import Annotaions.ServiceAction;
import DTO.StringResultDto;
import DTO.Comment.*;
import Exceptions.ActionFailedException;
import MainClasses.Comment;
import MainClasses.Post;
import MainClasses.User;
import Repositories.BannedUserRepository;
import Repositories.CommentRepository;
import Repositories.PostRepository;
import Repositories.SessionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentService {
    private static final CommentService instance = new CommentService();
    private final CommentRepository commentRepository = CommentRepository.getInstance();
    private final PostRepository postRepository = PostRepository.getInstance();
    private final SessionRepository sessionRepository = SessionRepository.getInstance();

    private CommentService() {}

    @ServiceAction
    public static CommentService getInstance() {
        return instance;
    }

    private void validateLeavingComment(User user) {
        BannedUserRepository.getInstance().isUserAllowedToComment(user.getId());
    }

    @ServiceAction
    public StringResultDto addComment(AddCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        validateLeavingComment(user);
        Comment comment = new Comment(
                user.getId(),
                data.getScript(),
                data.getPostId()
        );

        commentRepository.addComment(comment, comment.getPostId(),data.getPostOwnerId());

        Post post = postRepository.findPostById(comment.getPostId(), data.getPostOwnerId());
        validateLeavingComment(post);
        if (post.getCommentIds() == null) {
            post.setCommentIds(new java.util.HashSet<>());
        }
        post.getCommentIds().add(comment.getId());
        postRepository.editPost(post);
        return new StringResultDto(comment.getId());
    }

    private static void validateLeavingComment(Post post) {
        validateComment(!post.getCommentsAllowed(), "Comments are not allowed on post:" + post.getId());
    }

    @ServiceAction
    public void editComment(EditCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        Comment edit = commentRepository.findCommentById(data.getId(), data.getPostId());

        validateComment(!user.getId().equals(edit.getOwnerId()), "Comment is not owned by the logged in user");
        validateComment(data.getScript().isEmpty(), "script cannot be null");
        edit.setScript(data.getScript());
        commentRepository.editComment(edit,data.getPostId());

    }

    @ServiceAction
    public void deleteComment(DeleteCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        Comment del = commentRepository.findCommentById(data.getId(), data.getPostId());

        validateComment(!user.getId().equals(del.getOwnerId()), "Comment is not owned by the logged in user");
        commentRepository.removeComment(data.getId(), data.getPostId());

        Post post = postRepository.findPostById(data.getPostId(), data.getPostOwnerId());
        if (post != null && post.getCommentIds() != null) {
            post.getCommentIds().remove(data.getId());
            postRepository.editPost(post);
        }
    }

    private static void validateComment(boolean user, String actionName) {
        if (user) {
            throw new ActionFailedException(actionName);
        }
    }

    @ServiceAction
    public List<CommentDto> getAllCommentsByPostId(GetCommentsByPostDto data) {
        sessionRepository.validateSession(data.getSessionId());

        List<Comment> comments = commentRepository.getAllCommentsByPostId(data.getPostId());
        if (comments == null) {
            return Collections.emptyList();
        }

        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(mapCommentToDto(comment));
        }
        return dtos;
    }

    private CommentDto mapCommentToDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDto(
                comment.getId(),
                comment.getOwnerId(),
                comment.getScript(),
                comment.getPostId()
        );
    }
}
