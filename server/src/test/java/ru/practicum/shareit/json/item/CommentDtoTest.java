package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        JsonContent<CommentDto> result = json.write(
                new CommentDto(1L, "comment", 1L, "Test", LocalDateTime.of(2025, 1, 1, 12, 0, 0))
        );

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("comment");
        assertThat(result).extractingJsonPathNumberValue("$.item").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T12:00:00");
    }
}