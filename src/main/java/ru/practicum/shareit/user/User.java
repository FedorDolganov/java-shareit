package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
public class User {

    private long id;
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Почта пользователя введена некорректно")
    @NotBlank(message = "Почта пользователя не может быть пустым")
    private String email;

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}
