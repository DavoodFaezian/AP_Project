package Services;

import Annotaions.ServiceAction;
import DTO.GetIdsResultDto;
import DTO.Post.*;
import DTO.SessionIdDto;
import DTO.StringResultDto;
import DTO.User.GetPostsByUserDto;
import Exceptions.ActionFailedException;
import Exceptions.ItemNotFoundException;
import MainClasses.*;
import Repositories.*;

import java.util.*;
import java.util.stream.Collectors;

public class PostService {
    private static final PostService instance = new PostService();

    private final PostRepository postRepository = PostRepository.getInstance();
    private final CommentRepository commentRepository = CommentRepository.getInstance();
    private final PhotoRepository photoRepository = PhotoRepository.getInstance();
    private final AlbumRepository albumRepository = AlbumRepository.getInstance();
    private final UserRepository userRepository = UserRepository.getInstance();
    private final UserProfileRepository userProfileRepository = UserProfileRepository.getInstance();

    private PostService() {}

    @ServiceAction
    public static PostService getInstance() {
        return instance;
    }

    @ServiceAction
    private void validatePermission(User user) {
        BannedUserRepository.getInstance().isUserAllowedToPost(user.getId());
    }

    @ServiceAction
    public StringResultDto addPost(AddPostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);
        validatePermission(user);

        validatePhotoIdsAndAlbumIds(dto.getAlbumIds(), dto.getPhotoIds());
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

                findAlbum(ownerId, post, albumId);
            }
        }
        return new StringResultDto(post.getId());

    }

    private void findAlbum(String ownerId, Post post, String albumId) {
        Album album = albumRepository.findAlbumById(albumId, ownerId);

        if (album.getPostIds() == null) {
            album.setPostIds(new LinkedHashSet<>());
        }

        album.getPostIds().add(post.getId());
        albumRepository.editAlbum(album.getId(),album.getAlbumName(),album.getOwnerId());
    }

    private static void validatePhotoIdsAndAlbumIds(Set<String> dto, Set<String> dto1) {
        if (dto.isEmpty() && dto1.isEmpty()) {
            throw new ActionFailedException("Both photoIds and albumIds can't be null.");
        }
    }

    @ServiceAction
    public void editPost(EditPostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        validatePhotoIdsAndAlbumIds(dto.getAlbumIds(), dto.getPhotoIds());

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getId(), ownerId);

        Set<String> oldPhotoIds = getIds(post.getPhotoIds());

        Set<String> oldAlbumIds = getIds(post.getAlbumIds());


        Set<String> newPhotoIds = getIds(dto.getPhotoIds());

        Set<String> newAlbumIds = getIds(dto.getAlbumIds());


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

                albumRepository.editAlbum(album.getId(),album.getAlbumName(),album.getOwnerId());
            }
        }

        for (String albumId : newAlbumIds) {

            if (!oldAlbumIds.contains(albumId)) {

                findAlbum(ownerId, post, albumId);
            }
        }

        post.setPhotoIds(newPhotoIds);
        post.setAlbumIds(newAlbumIds);
        post.setAreCommentsAllowed(dto.getCommentsAllowed());

        postRepository.editPost(post);
    }

    private static Set<String> getIds(Set<String> post) {
        return post != null
                ? new LinkedHashSet<>(post)
                : new LinkedHashSet<>();
    }

    @ServiceAction
    public PostSetDto getAllPostsOfFollowings(SessionIdDto data) {
        User user = SessionRepository.getInstance().findUserBySessionId(data.getSessionId());
        Optional<UserProfile> profile = userProfileRepository.getUserProfileByUserId(user.getId());
        if(profile.isEmpty()){
            throw new ItemNotFoundException("UserProfile",user.getId());
        }
        Set<String> followingIds = profile.get().getFollowingsId();

        return new PostSetDto(followingIds.parallelStream()
                .map(postRepository::getPostsByOwnerId)
                .flatMap(Collection::stream)
                .map(PostDto::new)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(PostDto::getLastModified))
                )));
    }

    @ServiceAction
    public void deletePost(DeletePostDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

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

                albumRepository.editAlbum(album.getId(),album.getAlbumName(),album.getOwnerId());
            }
        }

        postRepository.removePost(post);
    }

    @ServiceAction
    public GetIdsResultDto getPhotoIdsOfPost(GetPostRelationsDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getPostId(), ownerId);

        return new GetIdsResultDto(post.getPhotoIds() != null
                ? post.getPhotoIds()
                : Collections.emptySet());
    }

    @ServiceAction
    public GetIdsResultDto getAlbumIdsOfPost(GetPostRelationsDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }

        String ownerId = user.getId();

        Post post = postRepository.findPostById(dto.getPostId(), ownerId);

        return new GetIdsResultDto(post.getAlbumIds() != null
                ? post.getAlbumIds()
                : Collections.emptySet());
    }

    @ServiceAction
    public PostListDto getAllPostsByOwnerId(GetPostsByOwnerDto dto) {

        String sessionId = dto.getSessionId();
        User user = SessionRepository.getInstance().findUserBySessionId(sessionId);

        if (user == null) {
            throw new IllegalStateException("User is not logged in.");
        }


        return new PostListDto(postRepository.getPostsByOwnerId(user.getId())
                .stream()
                .map(PostDto::new)
                .collect(Collectors.toList()));
    }
    @ServiceAction
    public PostListDto getPostsByUserId(GetPostsByUserDto dto) {
        User user = SessionRepository.getInstance().findUserBySessionId(dto.getSessionId());
        String userId = user.getId();
        if(dto.getUserId() != null){
            userId = dto.getUserId();
        }



        return new PostListDto(postRepository.getPostsByOwnerId(userId)
                .stream()
                .map(PostDto::new)
                .collect(Collectors.toList()));
    }

    
    public PostDto getPostById(String postId, String ownerId){
        Post post = postRepository.findPostById(postId,ownerId);
        return new PostDto(post);
    }
}
