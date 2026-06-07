package ru.practicum.shareit.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    private BookingClient bookingClient;

    @Test
    void add() throws Exception {
        when(bookingClient.bookItem(eq(1L), any(BookItemRequestDto.class))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(new BookItemRequestDto())
        );

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new BookItemRequestDto())
                        ))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).bookItem(eq(1L), any(BookItemRequestDto.class));
    }

    @Test
    void update() throws Exception {
        when(bookingClient.updateBooking(1L, true, 1L)).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(new BookItemRequestDto())
        );

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).updateBooking(1L, true, 1L);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingClient.updateBooking(1L, true, 1L)).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(new BookItemRequestDto())
        );

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBooking(1L, 1L);
    }

    @Test
    void getUsers() throws Exception {
        when(bookingClient.getBookings(1L, BookingState.ALL)).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(
                                Arrays.asList(
                                        new BookItemRequestDto(),
                                        new BookItemRequestDto()
                                )
                        )
        );

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getBookings(1L, BookingState.ALL);
    }

    @Test
    void getUserWithRandomState() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "RandomState"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBookings(anyLong(), any());
    }

    @Test
    void getOwner() throws Exception {
        when(bookingClient.getOwnerBookings(1L, BookingState.ALL)).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(
                                List.of(
                                        new BookItemRequestDto()
                                )
                        )
        );

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getOwnerBookings(1L, BookingState.ALL);
    }

    @Test
    void getOwnerWithRandomState() throws Exception {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "RandomState"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getOwnerBookings(anyLong(), any());
    }
}