package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.booking.services.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    private BookingService service;

    @PostMapping()
    public SendedBookingDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody BookingDto booking) {
        return service.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public SendedBookingDto update(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam boolean approved, @PathVariable long bookingId) {
        return service.updateApproved(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public SendedBookingDto get(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return service.getBooking(userId, bookingId);
    }

    @GetMapping()
    public List<SendedBookingDto> getUserBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(required = false) String state) {
        return service.getUserBookingsByState(userId, state);
    }

    @GetMapping("/owner")
    public List<SendedBookingDto> getOwnerBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam(required = false) String state) {
        return service.getOwnerBookingsByState(userId, state);
    }

}
