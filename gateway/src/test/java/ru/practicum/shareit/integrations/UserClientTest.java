package ru.practicum.shareit.integrations;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@SpringBootTest
class UserClientTest {

    @Autowired
    private UserClient userClient;

    private MockRestServiceServer mockServer;

    private final long userId = 1L;
    private final String api = "users";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(userClient.getRest());
    }

    @Test
    void getUser() {
        mockServer.expect(requestTo(containsString("/" + api + "/" + userId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = userClient.getUser(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void addUser() {
        mockServer.expect(requestTo(containsString("/" + api)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = userClient.addUser(new UserDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void updateUser() {
        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = userClient.updateUser(new PatchUserDto(), userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }

    @Test
    void deleteUser() {
        mockServer.expect(requestTo(containsString("/" + api + "/" + userId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        mockServer.verify();
    }
}