package DTO;

public class FollowAndUnfollowDto {

    private String followerSessionId;

    private String followingUserId;

    public FollowAndUnfollowDto(String followerSessionId, String followingUserId) {
        this.followerSessionId = followerSessionId;
        this.followingUserId = followingUserId;
    }

    public String getFollowerSessionId() {
        return followerSessionId;
    }

    public void setFollowerSessionId(String followerSessionId) {
        this.followerSessionId = followerSessionId;
    }

    public String getFollowingUserId() {
        return followingUserId;
    }

    public void setFollowingUserId(String followingUserId) {
        this.followingUserId = followingUserId;
    }
}
