package DTO;

import java.util.Set;

public record PostDto(
        String id,
        String ownerId,
        Set<String> photoIds,
        Set<String> albumIds,
        Set<String> sharedUserIds,
        Set<String> commentIds,
        Boolean commentsAllowed
) {}

