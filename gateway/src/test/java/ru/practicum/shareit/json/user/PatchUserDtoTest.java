package ru.practicum.shareit.json.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.PatchUserDto;

@JsonTest
class PatchUserDtoTest {

    @Autowired
    private JacksonTester<PatchUserDto> jacksonTester;

    @Test
    void testSerializePatchUserDto() throws Exception {
        PatchUserDto patchDto = new PatchUserDto("Updated Name", "updated@example.com");


        JsonContent<PatchUserDto> json = jacksonTester.write(patchDto);


        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathStringValue("$.name");
        assertThat(json).hasJsonPathStringValue("$.email");
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("Updated Name");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("updated@example.com");
    }
}