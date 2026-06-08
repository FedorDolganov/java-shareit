package ru.practicum.shareit.integrations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.DublicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Test
    void addUser_ShouldCreateAndReturnUser() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        assertThat(createdDto.getId()).isGreaterThan(0);
        assertThat(createdDto.getName()).isEqualTo("Test");
        assertThat(createdDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void addUser_ShouldThrowException_WhenEmailExists() {
        userService.addUser(new UserDto(0, "Test", "test@example.com"));

        assertThrows(DublicateException.class, () -> userService.addUser(new UserDto(0, "Test 2", "test@example.com")));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        UserDto updatedDto = userService.updateUser(new UserDto(0, "Test Updated", "test_updated@example.com"), createdDto.getId());

        assertThat(updatedDto.getId()).isEqualTo(createdDto.getId());
        assertThat(updatedDto.getName()).isEqualTo("Test Updated");
        assertThat(updatedDto.getEmail()).isEqualTo("test_updated@example.com");
    }

    @Test
    void updateUser_ShouldUpdateOnlyName() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        UserDto updatedDto = userService.updateUser(new UserDto(0, "Test Updated", null), createdDto.getId());

        assertThat(updatedDto.getName()).isEqualTo("Test Updated");
        assertThat(updatedDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void updateUser_ShouldUpdateOnlyEmail() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        UserDto updatedDto = userService.updateUser(new UserDto(0, null, "test_updated@example.com"), createdDto.getId());

        assertThat(updatedDto.getName()).isEqualTo("Test");
        assertThat(updatedDto.getEmail()).isEqualTo("test_updated@example.com");
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.updateUser(new UserDto(0, "Test", "test@example.com"), 999L));
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailExist() {
        userService.addUser(new UserDto(0, "Test", "test@example.com"));

        UserDto user = userService.addUser(new UserDto(0, "Test 2", "test-2@example.com"));

        assertThrows(DublicateException.class, () -> userService.updateUser(new UserDto(user.getId(), "Test 2", "test@example.com"), user.getId()));
    }

    @Test
    void getUser_ShouldReturnUser() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        UserDto foundDto = userService.getUser(createdDto.getId());

        assertThat(foundDto.getId()).isEqualTo(createdDto.getId());
        assertThat(foundDto.getName()).isEqualTo("Test");
        assertThat(foundDto.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        UserDto createdDto = userService.addUser(new UserDto(0, "Test", "test@example.com"));

        userService.deleteUser(createdDto.getId());

        assertThrows(NotFoundException.class, () -> userService.getUser(createdDto.getId()));
    }
}