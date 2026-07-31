package DTO.Comment;

public record CommentDto(
        String id,
        String ownerId,
        String script,
        String postId
) {}