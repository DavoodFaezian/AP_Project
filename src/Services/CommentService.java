package Services;

import DTO.AddResultDto;
import DTO.Comment.*;
import Exceptions.ActionFailedException;
import MainClasses.Comment;
import MainClasses.Post;
import MainClasses.User;
import Repositories.CommentRepository;
import Repositories.PostRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentService {
    private static final CommentService instance = new CommentService();
    private final CommentRepository commentRepository = CommentRepository.getInstance();
    private final PostRepository postRepository = PostRepository.getInstance();
    private final SessionRepository sessionRepository = SessionRepository.getInstance();

    private CommentService() {}

    public static CommentService getInstance() {
        return instance;
    }

    public AddResultDto addComment(AddCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        Comment comment = new Comment(
                user.getId(),
                data.getScript(),
                data.getPostId()
        );

        commentRepository.addComment(comment, comment.getPostId(),data.getPostOwnerId());

        Post post = postRepository.findPostById(comment.getPostId(), data.getPostOwnerId());
        if(!post.getCommentsAllowed()){
            throw new ActionFailedException("Comments are not allowed on post:"+post.getId());
        }
        if (post != null) {
            if (post.getCommentIds() == null) {
                post.setCommentIds(new java.util.HashSet<>());
            }
            post.getCommentIds().add(comment.getId());
            postRepository.editPost(post);
        }
        return new AddResultDto(comment.getId());
    }


    public void editComment(EditCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        Comment edit = commentRepository.findCommentById(data.getId(), data.getPostId());

        if(!user.getId().equals(edit.getOwnerId())){
            throw new ActionFailedException("Comment is not owned by the logged in user");
        }
        if(data.getScript().isEmpty()){
            throw new ActionFailedException("script cannot be null");
        }
        edit.setScript(data.getScript());
        commentRepository.editComment(edit,data.getPostId());

    }
    public void deleteComment(DeleteCommentDto data) {
        User user = sessionRepository.findUserBySessionId(data.getSessionId());
        Comment del = commentRepository.findCommentById(data.getId(), data.getPostId());

        if(!user.getId().equals(del.getOwnerId())){
            throw new ActionFailedException("Comment is not owned by the logged in user");
        }
        commentRepository.removeComment(data.getId(), data.getPostId());

        Post post = postRepository.findPostById(data.getPostId(), data.getPostOwnerId());
        if (post != null && post.getCommentIds() != null) {
            post.getCommentIds().remove(data.getId());
            postRepository.editPost(post);
        }
    }

    public List<CommentDto> getAllCommentsByPostId(GetCommentsByPostDto data) {
        sessionRepository.findUserBySessionId(data.getSessionId());

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
