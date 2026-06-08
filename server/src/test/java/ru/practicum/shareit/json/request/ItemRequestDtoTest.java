package ru.practicum.shareit.json.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Test request", 1L, LocalDateTime.of(2025, 1, 1, 12, 0, 0));

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test request");
        assertThat(result).extractingJsonPathNumberValue("$.requestor").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T12:00:00");
    }
}