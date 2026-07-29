package Services;

import MainClasses.Comment;
import Repositories.CommentRepository;
import ViewModels.Comment.CommentViewModel;

public class CommentService {

    private static final CommentService instance = new CommentService();

    private CommentService(){}

    public static CommentService getInstance() {
        return instance;
    }

    public static void editComment(CommentViewModel editedComment){

    }
}
