package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        JsonContent<ItemDto> result = json.write(
                new ItemDto(1L, "Test item", "Test desc", true, 1L)
        );

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test desc");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}