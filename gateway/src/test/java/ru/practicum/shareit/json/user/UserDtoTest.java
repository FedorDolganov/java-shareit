package ru.practicum.shareit.json.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto("John Doe", "john.doe@example.com");

        JsonContent<UserDto> json = jacksonTester.write(userDto);

        assertThat(json).isNotNull();
        assertThat(json).hasJsonPathStringValue("$.name");
        assertThat(json).hasJsonPathStringValue("$.email");
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@example.com");
    }
}