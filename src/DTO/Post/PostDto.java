package DTO.Post;

import java.util.Set;

public record PostDto(
        String ownerId,
        Set<String> photoIds,
        Set<String> albumIds,
        Set<String> sharedUserIds,
        Set<String> commentIds,
        Boolean commentsAllowed
) {}

