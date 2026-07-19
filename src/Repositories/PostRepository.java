package Repositories;

public class PostRepository extends BaseRepository{
    private static final PostRepository instance = new PostRepository();
    private PostRepository() {
        super("posts");
    }

    public static PostRepository getInstance() {
        return instance;
    }



}
