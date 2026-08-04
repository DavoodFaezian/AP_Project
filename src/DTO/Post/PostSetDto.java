package DTO.Post;

import java.util.List;
import java.util.Set;

public class PostSetDto {
    Set<PostDto> posts;

    public PostSetDto(Set<PostDto> posts) {
        this.posts = posts;
    }

    public Set<PostDto> getPosts() {
        return posts;
    }
}
