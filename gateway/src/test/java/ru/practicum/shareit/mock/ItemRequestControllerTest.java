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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    @Test
    void add() throws Exception {
        ItemRequestDto dto = new ItemRequestDto("Test", 1L);

        when(requestClient.addRequest(eq(1L), any(ItemRequestDto.class))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).addRequest(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void getByUser() throws Exception {
        when(requestClient.getRequestsByUser(eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(get("/requests", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequestsByUser(eq(1L));
    }

    @Test
    void getAll() throws Exception {
        when(requestClient.getAllRequests(eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(get("/requests/all", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getAllRequests(eq(1L));
    }

    @Test
    void getById() throws Exception {
        when(requestClient.getRequests(eq(1L), eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(requestClient, times(1)).getRequests(eq(1L), eq(1L));
    }
}