package Services;

import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import MainClasses.User;
import Repositories.PostRepository;
// Assuming other repositories and exception classes exist in your project structure
 import Repositories.CommentRepository;
 import Repositories.PhotoRepository;
 import Repositories.AlbumRepository;
 import Repositories.UserRepository;

import java.util.Collections;
import java.util.Set;
import java.util.List;

public class PostService {
    private static final PostService instance = new PostService();
    private final PostRepository postRepository = PostRepository.getInstance();
    // Inject or fetch other repositories as needed for relation management:
     private final CommentRepository commentRepository = CommentRepository.getInstance();
     private final PhotoRepository photoRepository = PhotoRepository.getInstance();
     private final AlbumRepository albumRepository = AlbumRepository.getInstance();
     private final UserRepository userRepository = UserRepository.getInstance();

    private PostService() {}

    public static PostService getInstance() {
        return instance;
    }

    public void addPost(Post post) {
        postRepository.addPost(post);


        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {
                Photo photo = photoRepository.findPhotoById(photoId, post.getOwnerId());
                photo.getPostIds().add(id);
                photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {
                Album album = albumRepository.findAlbumById(albumId, post.getOwnerId());
                album.getPostIds().add(id);
                albumRepository.editAlbum(album);
            }
        }

        if (post.getSharedUserIds() != null) {
            for (String userId : post.getSharedUserIds()) {
                User user = userRepository.findUserById(userId);
                user.getReceivedPostIds ().add(id);
                userRepository.editUser(user);
            }
        }
    }

    public void editPost(Post post) {
        postRepository.editPost(post);
    }

    public void deletePost(String id, String ownerId) {
        Post post = postRepository.findPostById(id, ownerId);

        if (post.getCommentIds() != null) {
            for (String commentId : post.getCommentIds()) {
                 commentRepository.removeComment(commentId, ownerId);
            }
        }

        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {
                 Photo photo = photoRepository.findPhotoById(photoId, ownerId);
                 photo.getPostIds().remove(id);
                 photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {
                 Album album = albumRepository.findAlbumById(albumId, ownerId);
                 album.getPostIds().remove(id);
                 albumRepository.editAlbum(album);
            }
        }

        if (post.getSharedUserIds() != null) {
            for (String userId : post.getSharedUserIds()) {
                 User user = userRepository.findUserById(userId);
                 user.getReceivedPostIds ().remove(id);
                 userRepository.editUser(user);
            }
        }

        postRepository.removePost(post);
    }

    public Set<String> getPhotoIdsOfPost(String postId, String ownerId) {
        Post post = postRepository.findPostById(postId, ownerId);
        return post.getPhotoIds() != null ? post.getPhotoIds() : Collections.emptySet();
    }

    public Set<String> getAlbumIdsOfPost(String postId, String ownerId) {
        Post post = postRepository.findPostById(postId, ownerId);
        return post.getAlbumIds() != null ? post.getAlbumIds() : Collections.emptySet();
    }

    public List<Post> getAllPostsByOwnerId(String ownerId) {
        return postRepository.getPostsByOwnerId(ownerId);
    }
}