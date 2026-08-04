package DTO.User;

import java.util.List;

public class UserListDto {
    List<GetUserDto> users;

    public UserListDto(List<GetUserDto> users) {
        this.users = users;
    }

    public List<GetUserDto> getUsers() {
        return users;
    }
}
