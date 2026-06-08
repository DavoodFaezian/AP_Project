package Services;

import MainClasses.Comment;
import Repositories.CommentRepository;
import ViewModels.Comment.CommentViewModel;

public class CommentService {
    public static void editComment(CommentViewModel editedComment){
        Comment comment = CommentRepository.getInstance().findCommentById(editedComment.getId());
        comment.editComment(editedComment.getScript());
    }
}
