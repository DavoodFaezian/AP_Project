package Services;

import DTO.Comment.CommentDto;
import DTO.Comment.AddCommentDto;
import DTO.Comment.DeleteCommentDto;
import DTO.Comment.GetCommentsByPostDto;
import MainClasses.Comment;
import MainClasses.Post;
import Repositories.CommentRepository;
import Repositories.PostRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentService {
    private static final CommentService instance = new CommentService();
    private final CommentRepository commentRepository = CommentRepository.getInstance();
    private final PostRepository postRepository = PostRepository.getInstance();

    private CommentService() {}

    public static CommentService getInstance() {
        return instance;
    }

    public void addComment(AddCommentDto data) {
        Comment comment = new Comment(
                data.getOwnerId(),
                data.getScript(),
                data.getPostId()
        );

        commentRepository.addComment(comment, comment.getPostId());

        Post post = postRepository.findPostById(comment.getPostId(), data.getPostOwnerId());
        if (post != null) {
            if (post.getCommentIds() == null) {
                post.setCommentIds(new java.util.HashSet<>());
            }
            post.getCommentIds().add(comment.getId());
            postRepository.editPost(post);
        }
    }

    public void deleteComment(DeleteCommentDto data) {
        commentRepository.removeComment(data.getId(), data.getPostId());

        Post post = postRepository.findPostById(data.getPostId(), data.getPostOwnerId());
        if (post != null && post.getCommentIds() != null) {
            post.getCommentIds().remove(data.getId());
            postRepository.editPost(post);
        }
    }

    public List<CommentDto> getAllCommentsByPostId(GetCommentsByPostDto data) {
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
