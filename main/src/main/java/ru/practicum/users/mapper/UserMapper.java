package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User fromDto(UserDto userDto);

    UserDto toDto(User user);

    List<UserDto> toDtos(List<User> users);

    User fromNewUserDto(NewUserRequest userRequest);

}

