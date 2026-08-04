package DTO.User;

import java.util.Set;

public class GetUserDto {
    private String userId;
    private String userName;
    private String profilePhotoName;
    private Set<String> followerIds;
    private Set<String> followingIds;

    public GetUserDto(String userId, String userName, String profilePhotoName,Set<String> followerIds,
                      Set<String> followingIds) {
        this.userId = userId;
        this.userName = userName;
        this.profilePhotoName = profilePhotoName;
        this.followerIds = followerIds;
        this.followingIds = followingIds;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getProfilePhotoName() {
        return profilePhotoName;
    }
}
