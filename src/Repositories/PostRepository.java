package Repositories;

import Exceptions.ItemNotFoundException;
import MainClasses.Post;

import java.util.List;
import java.util.Optional;

public class PostRepository extends BaseRepository<Post> {
    private static PostRepository instance = new PostRepository();

    private PostRepository() {
        super("posts");
    }

    public static PostRepository getInstance() {
        return instance;
    }

    public void addPost(Post post) {
        var postFileManager = getFileManager(post.getOwnerId());
        postFileManager.addToList(post);
    }

    public void removePost(Post post) {
        var postFileManager = getFileManager(post.getOwnerId());
        postFileManager.removeFromList(post);
    }

    public void removePost(String id, String ownerId) {
        Post remove = findPostById(id, ownerId);
        removePost(remove);
    }

    public void editPost(Post edit) {
        var postFileManager = getFileManager(edit.getOwnerId());
        postFileManager.edit(edit);
    }

    public List<Post> getPostsByOwnerId(String ownerId) {
        var postFileManager = getFileManager(ownerId);
        return postFileManager.getAll();
    }

    public Post findPostById(String id, String ownerId) {
        Optional<Post> post = getFileManager(ownerId).findItemById(id);
        if (post.isEmpty()) {
            throw new ItemNotFoundException("post", id);
        }
        return post.get();
    }

    public boolean isPostIdValid(String postId, String ownerId) {
        return getFileManager(ownerId).exists(p -> p.getId().equals(postId));
    }
}