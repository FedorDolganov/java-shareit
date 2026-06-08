package ru.practicum.shareit.json.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@JsonTest
class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void testSerializeItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Need a drill for renovation", 5L);

        JsonContent<ItemRequestDto> json = jacksonTester.write(itemRequestDto);

        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathStringValue("$.description");
        assertThat(json).hasJsonPathNumberValue("$.requestor");
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill for renovation");
        assertThat(json).extractingJsonPathNumberValue("$.requestor").isEqualTo(5);
    }
}