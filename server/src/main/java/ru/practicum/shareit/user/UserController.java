package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto user) {
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody UserDto user) {
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

}
