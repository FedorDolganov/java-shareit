package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddBookingDto {

    @NotNull(message = "Дата начала брони не может быть пустой")
    private LocalDateTime start;
    @NotNull(message = "Дата конца брони не может быть пустой")
    private LocalDateTime end;
    @NotNull(message = "Индефикатор предмета не может быть пустым")
    private long itemId;

}
