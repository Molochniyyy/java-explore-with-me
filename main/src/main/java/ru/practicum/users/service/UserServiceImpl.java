package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.mapper.UserMapper;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest user) {
        return userMapper.toDto(userRepository.save(userMapper.fromNewUserRequest(user)));
    }

    @Override
    public Collection<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        return userRepository.findByIdIn(ids, pageable).stream()
                .map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.deleteById(userId);
    }
}
