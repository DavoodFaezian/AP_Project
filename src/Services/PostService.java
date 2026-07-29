package Services;

import DTO.Post.PostDto;
import DTO.Post.EditPostDto;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import MainClasses.User;
import Repositories.PostRepository;
import Repositories.CommentRepository;
import Repositories.PhotoRepository;
import Repositories.AlbumRepository;
import Repositories.UserRepository;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class PostService {
    private static final PostService instance = new PostService();
    private final PostRepository postRepository = PostRepository.getInstance();
    private final CommentRepository commentRepository = CommentRepository.getInstance();
    private final PhotoRepository photoRepository = PhotoRepository.getInstance();
    private final AlbumRepository albumRepository = AlbumRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();

    private PostService() {}

    public static PostService getInstance() {
        return instance;
    }

    public void addPost(PostDto dto) {
        Post post = new Post(
                dto.ownerId(),
                dto.photoIds() != null ? dto.photoIds() : new HashSet<>(),
                dto.albumIds() != null ? dto.albumIds() : new HashSet<>(),
                dto.sharedUserIds() != null ? dto.sharedUserIds() : new HashSet<>(),
                dto.commentIds() != null ? dto.commentIds() : new HashSet<>(),
                dto.commentsAllowed()
        );

        postRepository.addPost(post);

        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {
                Photo photo = photoRepository.findPhotoById(photoId, post.getOwnerId());
                photo.getPostIds().add(post.getId());
                photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {
                Album album = albumRepository.findAlbumById(albumId, post.getOwnerId());
                album.getPostIds().add(post.getId());
                albumRepository.editAlbum(album);
            }
        }

        if (post.getSharedUserIds() != null) {
            for (String userId : post.getSharedUserIds()) {
                User user = userRepository.findUserById(userId);
                user.getReceivedPostIds().add(post.getId());
                userRepository.editUser(user);
            }
        }
    }

    public void editPost(EditPostDto dto) {
        Post post = postRepository.findPostById(dto.id(), dto.ownerId());

        post.setOwnerId(dto.ownerId());
        post.setPhotoIds(dto.photoIds());
        post.setAlbumIds(dto.albumIds());
        post.setSharedUserIds(dto.sharedUserIds());
        post.setCommentIds(dto.commentIds());
        post.setAreCommentsAllowed(dto.commentsAllowed());

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
                user.getReceivedPostIds().remove(id);
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