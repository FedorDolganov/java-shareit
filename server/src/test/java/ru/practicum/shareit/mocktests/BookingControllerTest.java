package ru.practicum.shareit.mocktests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.booking.services.BookingService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService service;

    @Test
    void add_ShouldCreateAndReturnBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.addBooking(any(BookingDto.class), eq(1L))).thenReturn(
                new SendedBookingDto(1L, start, end, null, null, BookingStatus.WAITING)
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookingDto(-1L, start, end, 1L, null))
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(service, times(1)).addBooking(any(BookingDto.class), eq(1L));
    }

    @Test
    void update_ShouldUpdateAndReturnBooking() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.updateApproved(1L, true, 1L)).thenReturn(
                new SendedBookingDto(1L, start, end, null, null, BookingStatus.APPROVED)
        );

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(service, times(1)).updateApproved(1L, true, 1L);
    }

    @Test
    void get_ShouldReturnBookingById() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.getBooking(1L, 1L)).thenReturn(
                new SendedBookingDto(1L, start, end, null, null, BookingStatus.APPROVED)
        );

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(service, times(1)).getBooking(1L, 1L);
    }

    @Test
    void getUserBooking_ShouldReturnUserBookings() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.getUserBookingsByState(1L, "ALL")).thenReturn(
                Arrays.asList(
                        new SendedBookingDto(1L, start, end, null, null, BookingStatus.APPROVED),
                        new SendedBookingDto(2L, start.plusDays(1), end.plusDays(1), null, null, BookingStatus.WAITING)
                )
        );

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("WAITING"));

        verify(service, times(1)).getUserBookingsByState(1L, "ALL");
    }

    @Test
    void getUserBooking_ShouldReturnUserBookingsWithDefaultState() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.getUserBookingsByState(eq(1L), any())).thenReturn(
                List.of(
                        new SendedBookingDto(1L, start, end, null, null, BookingStatus.WAITING)
                )
        );

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(service, times(1)).getUserBookingsByState(eq(1L), any());
    }

    @Test
    void getOwnerBooking_ShouldReturnOwnerBookings() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.getOwnerBookingsByState(1L, "CURRENT")).thenReturn(
                List.of(
                        new SendedBookingDto(1L, start, end, null, null, BookingStatus.APPROVED)
                )
        );

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("APPROVED"));

        verify(service, times(1)).getOwnerBookingsByState(1L, "CURRENT");
    }

    @Test
    void getOwnerBooking_ShouldReturnOwnerBookingsWithDefaultState() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        when(service.getOwnerBookingsByState(eq(1L), any())).thenReturn(
                List.of(
                        new SendedBookingDto(1L, start, end, null, null, BookingStatus.WAITING)
                )
        );

        mockMvc.perform(get("/bookings/owner?state=" + BookingState.ALL.name().toLowerCase())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(service, times(1)).getOwnerBookingsByState(eq(1L), any());
    }
}