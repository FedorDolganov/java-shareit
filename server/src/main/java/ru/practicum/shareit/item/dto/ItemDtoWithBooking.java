package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {

    private long id;
    private String name;
    private String description;
    private boolean available;
    private Long request;
    private List<CommentDto> comments;
    private BookingDto nextBooking;
    private BookingDto lastBooking;

}
