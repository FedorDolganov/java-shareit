package ru.practicum.shareit.json.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@JsonTest
class BookItemRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> jacksonTester;

    @Test
    void testSerializeBookItemRequestDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 20, 18, 0);
        BookItemRequestDto requestDto = new BookItemRequestDto(42L, start, end);

        JsonContent<BookItemRequestDto> json = jacksonTester.write(requestDto);

        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathNumberValue("$.itemId");
        assertThat(json).hasJsonPathStringValue("$.start");
        assertThat(json).hasJsonPathStringValue("$.end");
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(42);
        assertThat(json).extractingJsonPathStringValue("$.start").isEqualTo("2025-01-15T10:00:00");
        assertThat(json).extractingJsonPathStringValue("$.end").isEqualTo("2025-01-20T18:00:00");
    }
}