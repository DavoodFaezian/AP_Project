package DTO.Comment;

public class DeleteCommentDto {
    private String id;
    private String postId;
    private String postOwnerId;

    public DeleteCommentDto(String id, String postId, String postOwnerId) {
        this.id = id;
        this.postId = postId;
        this.postOwnerId = postOwnerId;
    }

    public String getId() {
        return id;
    }

    public String getPostId() {
        return postId;
    }

    public String getPostOwnerId() {
        return postOwnerId;
    }
}
