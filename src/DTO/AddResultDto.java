package DTO;

public class AddResultDto {
    private String id;

    public AddResultDto(String creationId) {
        this.id = creationId;
    }

    public String getId() {
        return id;
    }
}
