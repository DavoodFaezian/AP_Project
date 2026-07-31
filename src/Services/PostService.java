package Services;

import DTO.Post.DeletePostDto;
import DTO.Post.EditPostDto;
import DTO.Post.GetPostRelationsDto;
import DTO.Post.GetPostsByOwnerDto;
import DTO.Post.PostDto;
import MainClasses.Album;
import MainClasses.Photo;
import MainClasses.Post;
import MainClasses.User;
import Repositories.AlbumRepository;
import Repositories.CommentRepository;
import Repositories.PhotoRepository;
import Repositories.PostRepository;
import Repositories.SessionRepository;
import Repositories.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = new Post(
                ownerId,
                dto.getPhotoIds() != null ? new HashSet<>(dto.getPhotoIds()) : new HashSet<>(),
                dto.getAlbumIds() != null ? new HashSet<>(dto.getAlbumIds()) : new HashSet<>(),
                dto.getCommentsAllowed()
        );

        postRepository.addPost(post);

        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPostIds() == null) {
                    photo.setPostIds(new HashSet<>());
                }

                photo.getPostIds().add(post.getId());
                photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {

                Album album = albumRepository.findAlbumById(albumId, ownerId);

                if (album.getPostIds() == null) {
                    album.setPostIds(new HashSet<>());
                }

                album.getPostIds().add(post.getId());
                albumRepository.editAlbum(album);
            }
        }

    }

    public void editPost(EditPostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getId(), ownerId);

        Set<String> oldPhotoIds = post.getPhotoIds() != null
                ? new HashSet<>(post.getPhotoIds())
                : new HashSet<>();

        Set<String> oldAlbumIds = post.getAlbumIds() != null
                ? new HashSet<>(post.getAlbumIds())
                : new HashSet<>();


        Set<String> newPhotoIds = dto.getPhotoIds() != null
                ? new HashSet<>(dto.getPhotoIds())
                : new HashSet<>();

        Set<String> newAlbumIds = dto.getAlbumIds() != null
                ? new HashSet<>(dto.getAlbumIds())
                : new HashSet<>();



        for (String photoId : oldPhotoIds) {

            if (!newPhotoIds.contains(photoId)) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPostIds() != null) {
                    photo.getPostIds().remove(post.getId());
                }

                photoRepository.editPhoto(photo);
            }
        }

        for (String photoId : newPhotoIds) {

            if (!oldPhotoIds.contains(photoId)) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPostIds() == null) {
                    photo.setPostIds(new HashSet<>());
                }

                photo.getPostIds().add(post.getId());
                photoRepository.editPhoto(photo);
            }
        }


        for (String albumId : oldAlbumIds) {

            if (!newAlbumIds.contains(albumId)) {

                Album album = albumRepository.findAlbumById(albumId, ownerId);

                if (album.getPostIds() != null) {
                    album.getPostIds().remove(post.getId());
                }

                albumRepository.editAlbum(album);
            }
        }

        for (String albumId : newAlbumIds) {

            if (!oldAlbumIds.contains(albumId)) {

                Album album = albumRepository.findAlbumById(albumId, ownerId);

                if (album.getPostIds() == null) {
                    album.setPostIds(new HashSet<>());
                }

                album.getPostIds().add(post.getId());
                albumRepository.editAlbum(album);
            }
        }






        post.setPhotoIds(newPhotoIds);
        post.setAlbumIds(newAlbumIds);
        post.setAreCommentsAllowed(dto.getCommentsAllowed());

        postRepository.editPost(post);
    }

    public void deletePost(DeletePostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getId(), ownerId);

        if (post.getCommentIds() != null) {
            for (String commentId : post.getCommentIds()) {

                commentRepository.removeComment(commentId, post.getId());
            }
        }

        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPostIds() != null) {
                    photo.getPostIds().remove(post.getId());
                }

                photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {

                Album album = albumRepository.findAlbumById(albumId, ownerId);

                if (album.getPostIds() != null) {
                    album.getPostIds().remove(post.getId());
                }

                albumRepository.editAlbum(album);
            }
        }

        if (post.getSharedUserIds() != null) {
            for (String sharedUserId : post.getSharedUserIds()) {

                User sharedUser = userRepository.findUserById(sharedUserId);

                if (sharedUser.getReceivedPostIds() != null) {
                    sharedUser.getReceivedPostIds().remove(post.getId());
                }

                userRepository.editUser(sharedUser);
            }
        }

        postRepository.removePost(post);
    }

    public Set<String> getPhotoIdsOfPost(GetPostRelationsDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getPostId(), ownerId);

        return post.getPhotoIds() != null
                ? post.getPhotoIds()
                : Collections.emptySet();
    }

    public Set<String> getAlbumIdsOfPost(GetPostRelationsDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getPostId(), ownerId);

        return post.getAlbumIds() != null
                ? post.getAlbumIds()
                : Collections.emptySet();
    }

    public List<Post> getAllPostsByOwnerId(GetPostsByOwnerDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        return postRepository.getPostsByOwnerId(user.getId());
    }
}
