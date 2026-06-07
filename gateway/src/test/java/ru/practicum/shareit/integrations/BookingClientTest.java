package ru.practicum.shareit.integrations;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@SpringBootTest
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    private MockRestServiceServer mockServer;

    private final long userId = 1L;
    private final String api = "bookings";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(bookingClient.getRest());
    }

    @Test
    void getBookings() {
        mockServer.expect(requestTo(containsString("/" + api)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(queryParam("state", "ALL"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.getBookings(userId, BookingState.ALL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }


    @Test
    void bookItem() {
        mockServer.expect(requestTo(containsString("/" + api)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.bookItem(userId, new BookItemRequestDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }


    @Test
    void getBooking() {
        long bookingId = 1L;

        mockServer.expect(requestTo(containsString("/" + api + "/" + bookingId)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.getBooking(userId, bookingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void getOwnerBookings() {
        mockServer.expect(requestTo(containsString("/" + api + "/owner")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(queryParam("state", "ALL"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.getOwnerBookings(userId, BookingState.ALL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void updateBooking() {
        long bookingId = 1L;

        boolean approved = true;

        mockServer.expect(requestTo(containsString("/" + api + "/" + bookingId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(queryParam("approved", "true"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = bookingClient.updateBooking(userId, approved, bookingId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

}