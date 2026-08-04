package DTO;

import java.util.Set;

public class GetIdsResultDto {
    Set<String> ids;

    public GetIdsResultDto(Set<String> ids) {
        this.ids = ids;
    }

    public Set<String> getIds() {
        return ids;
    }
}
