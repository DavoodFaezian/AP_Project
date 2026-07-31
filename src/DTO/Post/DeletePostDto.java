package DTO.Post;

public class DeletePostDto {

    private final String sessionId;
    private final String id;

    public DeletePostDto(String sessionId, String id) {
        this.sessionId = sessionId;
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getId() {
        return id;
    }
}
