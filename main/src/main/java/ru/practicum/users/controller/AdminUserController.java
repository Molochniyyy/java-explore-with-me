package ru.practicum.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.model.NewUserRequest;
import ru.practicum.users.service.UserService;
import ru.practicum.utils.ControllerLog;
import ru.practicum.utils.Create;

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
    public ResponseEntity<UserDto> addUser(@Validated({Create.class}) @RequestBody NewUserRequest userDto,
                                           HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return new ResponseEntity<>(userService.addUser(userDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                                           HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Collection<UserDto>> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(name = "from", defaultValue = "10") Integer size,
                                                        HttpServletRequest request) {
        log.info("{}", ControllerLog.createUrlInfo(request));
        return new ResponseEntity<>(userService.findUsers(ids, from, size), HttpStatus.OK);
    }
}
