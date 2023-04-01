package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.IgnoreJRERequirement;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.service.UserService;
import ru.practicum.utils.ControllerLog;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Slf4j
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody NewUserRequest userDto,
                           HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return userService.addUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId,
                           HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        userService.deleteUser(userId);
    }

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "from", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return userService.findUsers(ids, from, size);
    }
}
