package ru.practicum.users.service;

import org.springframework.stereotype.Service;
import ru.practicum.users.dto.NewUserRequest;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.User;

import java.util.Collection;
import java.util.List;

@Service
public interface UserService {
    UserDto addUser(NewUserRequest userDto);

    Collection<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

    User getById(Long id);
}
