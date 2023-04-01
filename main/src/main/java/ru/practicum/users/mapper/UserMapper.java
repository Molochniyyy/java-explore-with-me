package ru.practicum.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    UserShortDto toShortDto(User user);

    User fromDto(UserDto userDto);

    User fromShortDto(UserShortDto userShortDto);

    User fromNewUserRequest(NewUserRequest userRequest);
}
