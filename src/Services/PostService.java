package Services;

import DTO.Post.*;
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

import java.util.*;
import java.util.stream.Collectors;

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

    public void addPost(AddPostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = new Post(
                ownerId,
                dto.getPhotoIds() != null ? new LinkedHashSet<>(dto.getPhotoIds()) : new LinkedHashSet<>(),
                dto.getAlbumIds() != null ? new LinkedHashSet<>(dto.getAlbumIds()) : new LinkedHashSet<>(),
                dto.getCommentsAllowed()
        );

        postRepository.addPost(post);

        if (post.getPhotoIds() != null) {
            for (String photoId : post.getPhotoIds()) {

                Photo photo = photoRepository.findPhotoById(photoId, ownerId);

                if (photo.getPostIds() == null) {
                    photo.setPostIds(new LinkedHashSet<>());
                }

                photo.getPostIds().add(post.getId());
                photoRepository.editPhoto(photo);
            }
        }

        if (post.getAlbumIds() != null) {
            for (String albumId : post.getAlbumIds()) {

                Album album = albumRepository.findAlbumById(albumId, ownerId);

                if (album.getPostIds() == null) {
                    album.setPostIds(new LinkedHashSet<>());
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
                ? new LinkedHashSet<>(post.getPhotoIds())
                : new LinkedHashSet<>();

        Set<String> oldAlbumIds = post.getAlbumIds() != null
                ? new LinkedHashSet<>(post.getAlbumIds())
                : new LinkedHashSet<>();


        Set<String> newPhotoIds = dto.getPhotoIds() != null
                ? new LinkedHashSet<>(dto.getPhotoIds())
                : new LinkedHashSet<>();

        Set<String> newAlbumIds = dto.getAlbumIds() != null
                ? new LinkedHashSet<>(dto.getAlbumIds())
                : new LinkedHashSet<>();



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
                    photo.setPostIds(new LinkedHashSet<>());
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
                    album.setPostIds(new LinkedHashSet<>());
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
//    public Set<PostDto> getAllPostsOfFollowings(String userId) {
//        User user = userRepository.findUserById(userId);
//        Set<String> followingIds = user.getFollowingsId();
//
//        return followingIds.parallelStream()
//                .map(postRepository::getPostsByOwnerId)
//                .flatMap(Collection::stream)
//                .map(PostDto::new)
//                .collect(Collectors.toCollection(() ->
//                        new TreeSet<>(Comparator.comparing(PostDto::getLastModified))
//                ));
//    }


    public void deletePost(DeletePostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getId(), ownerId);

        if (post.getCommentIds() != null) {
            commentRepository.removeCommentsByPostId(post.getId());
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

    public List<PostDto> getAllPostsByOwnerId(GetPostsByOwnerDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        return postRepository.getPostsByOwnerId(user.getId())
                .stream()
                .map(PostDto::new)
                .collect(Collectors.toList());
    }
}
