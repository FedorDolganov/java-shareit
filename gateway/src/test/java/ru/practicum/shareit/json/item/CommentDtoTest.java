package ru.practicum.shareit.json.item;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    void testSerializeCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto("Great item, very useful!");

        JsonContent<CommentDto> json = jacksonTester.write(commentDto);

        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathStringValue("$.text");
        assertThat(json).extractingJsonPathStringValue("$.text").isEqualTo("Great item, very useful!");
    }
}