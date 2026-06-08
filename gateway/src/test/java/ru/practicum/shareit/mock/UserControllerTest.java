package ru.practicum.shareit.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.PatchUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;


    @Test
    void getUser() throws Exception {
        when(userClient.getUser(eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(get("/users/{userId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(userClient, times(1)).getUser(eq(1L));
    }

    @Test
    void addUser() throws Exception {
        UserDto dto = new UserDto("Test", "test@email.com");

        when(userClient.addUser(any(UserDto.class))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(post("/users", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).addUser(any(UserDto.class));
    }

    @Test
    void updateUser() throws Exception {
        PatchUserDto dto = new PatchUserDto("Test", "test@email.com");

        when(userClient.updateUser(any(PatchUserDto.class), eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient, times(1)).updateUser(any(PatchUserDto.class), eq(1L));
    }

    @Test
    void deleteUser() throws Exception {
        when(userClient.deleteUser(eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(delete("/users/{userId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(userClient, times(1)).deleteUser(eq(1L));
    }
}