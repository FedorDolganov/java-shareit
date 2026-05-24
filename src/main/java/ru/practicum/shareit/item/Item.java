package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Item {

    private long id;
    @NotBlank(message = "Имя предмета не может быть пустым")
    private String name;
    @NotBlank(message = "Описание предмета не может быть пустым")
    private String description;
    @NotNull(message = "Доступность предмета не может быть пустой")
    private Boolean available;
    private long owner;
    private long request;

}
