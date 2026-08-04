package DTO.Comment;

import java.util.List;

public class CommentListDto {
    private List<CommentDto> comments;

    public CommentListDto(List<CommentDto> comments) {
        this.comments = comments;
    }

    public List<CommentDto> getComments() {
        return comments;
    }
}
