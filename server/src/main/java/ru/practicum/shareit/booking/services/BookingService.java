package ru.practicum.shareit.booking.services;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;

import java.util.List;

public interface BookingService {

    SendedBookingDto addBooking(BookingDto booking, long userId);

    SendedBookingDto updateApproved(long userId, boolean approved, long bookingId);

    SendedBookingDto getBooking(long userId, long bookingId);

    List<SendedBookingDto> getUserBookingsByState(long userId, String state);

    List<SendedBookingDto> getOwnerBookingsByState(long userId, String state);
}
