package ru.practicum.shareit.json.item;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto("Hammer", "Heavy duty hammer", true, 10L);

        JsonContent<ItemDto> json = jacksonTester.write(itemDto);

        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathStringValue("$.name");
        assertThat(json).hasJsonPathStringValue("$.description");
        assertThat(json).hasJsonPathBooleanValue("$.available");
        assertThat(json).hasJsonPathNumberValue("$.requestId");
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Hammer");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Heavy duty hammer");
        assertThat(json).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(json).extractingJsonPathNumberValue("$.requestId").isEqualTo(10);
    }
}