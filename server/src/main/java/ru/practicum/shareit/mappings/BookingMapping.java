package ru.practicum.shareit.mappings;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@UtilityClass
public class BookingMapping {

    public static BookingDto from(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getStatus()
        );
    }

    public static SendedBookingDto toSended(Booking booking) {
        return new SendedBookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking to(BookingDto booking, Item item, User user) {
        return new Booking(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                booking.getStatus()
        );
    }

}
