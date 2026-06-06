package ru.practicum.shareit.mocktests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;


    @Test
    void get_ShouldReturnUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(new UserDto(1L, "Test", "test@example.com"));

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void add_ShouldCreateAndReturnUser() throws Exception {
        when(userService.addUser(any(UserDto.class))).thenReturn(new UserDto(1L, "Test", "test@example.com"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(-1L, "Test", "test@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    void update_ShouldUpdateAndReturnUser() throws Exception {
        when(userService.updateUser(any(UserDto.class), eq(1L))).thenReturn(new UserDto(1L, "Test Updated", "test_updated@example.com"));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDto(-1L, "Test Updated", "test_updated@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Updated"))
                .andExpect(jsonPath("$.email").value("test_updated@example.com"));

        verify(userService, times(1)).updateUser(any(UserDto.class), eq(1L));
    }

    @Test
    void delete_ShouldReturnOk() throws Exception {
        long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(userId);
    }
}