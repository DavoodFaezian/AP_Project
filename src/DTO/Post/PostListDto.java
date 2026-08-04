package DTO.Post;

import java.util.List;

public class PostListDto {
    List<PostDto> posts;

    public PostListDto(List<PostDto> posts) {
        this.posts = posts;
    }

    public List<PostDto> getPosts() {
        return posts;
    }
}
