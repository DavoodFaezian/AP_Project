package DTO.Comment;

import java.util.Objects;

public class CommentDto {
    private final String id;
    private final String ownerId;
    private final String script;
    private final String postId;

    public CommentDto(
            String id,
            String ownerId,
            String script,
            String postId
    ) {
        this.id = id;
        this.ownerId = ownerId;
        this.script = script;
        this.postId = postId;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getScript() {
        return script;
    }

    public String getPostId() {
        return postId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CommentDto) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.ownerId, that.ownerId) &&
                Objects.equals(this.script, that.script) &&
                Objects.equals(this.postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerId, script, postId);
    }

    @Override
    public String toString() {
        return "CommentDto[" +
                "id=" + id + ", " +
                "ownerId=" + ownerId + ", " +
                "script=" + script + ", " +
                "postId=" + postId + ']';
    }
}