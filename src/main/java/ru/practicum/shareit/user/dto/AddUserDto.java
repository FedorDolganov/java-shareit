package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddUserDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;
    @Email(message = "Почта пользователя введена некорректно")
    @NotBlank(message = "Почта пользователя не может быть пустой")
    private String email;

}
