package ru.practicum.shareit.json.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class SendedBookingDtoTest {

    @Autowired
    private JacksonTester<SendedBookingDto> json;

    @Test
    void testSerialize() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Test");

        SendedBookingDto bookingDto = new SendedBookingDto(
                1L,
                LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                LocalDateTime.of(2025, 1, 1, 15, 0, 0),
                item,
                booker,
                BookingStatus.WAITING
        );

        JsonContent<SendedBookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-01T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-01T15:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Test item");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}