package DTO;

public class SearchDto {
    private String sessionId;
    private String query;

    public SearchDto(String sessionId, String query) {
        this.sessionId = sessionId;
        this.query = query;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getQuery() {
        return query;
    }
}