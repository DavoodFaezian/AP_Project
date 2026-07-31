package DTO.Comment;

public class GetCommentsByPostDto {
    private String postId;

    public GetCommentsByPostDto(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }
}
