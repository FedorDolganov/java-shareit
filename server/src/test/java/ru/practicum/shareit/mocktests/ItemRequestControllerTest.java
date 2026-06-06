package ru.practicum.shareit.mocktests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void add_ShouldCreateAndReturnRequest() throws Exception {
        when(itemRequestService.add(eq(1L), any(ItemRequestDto.class))).thenReturn(new ItemRequestDto(1L, "Test request", -1L, null));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemRequestDto(-1L, "Test request", -1L, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test request"));

        verify(itemRequestService, times(1)).add(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void getRequestsByUser_ShouldReturnListOfRequests() throws Exception {
        when(itemRequestService.getByUser(1L)).thenReturn(
                Arrays.asList(
                        new SendedItemRequestDto(1L, "Test request", -1L, null, new ArrayList<>()),
                        new SendedItemRequestDto(2L, "Test request 2", -1L, null, new ArrayList<>())
                )
        );

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test request"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Test request 2"));

        verify(itemRequestService, times(1)).getByUser(1L);
    }

    @Test
    void getAll_ShouldReturnListOfAllRequests() throws Exception {
        when(itemRequestService.getAll()).thenReturn(
                Arrays.asList(
                        new ItemRequestDto(1L, "Test request", -1L, null),
                        new ItemRequestDto(2L, "Test request 2", -1L, null)
                )
        );

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Test request"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Test request 2"));

        verify(itemRequestService, times(1)).getAll();
    }

    @Test
    void get_ShouldReturnRequestById() throws Exception {
        when(itemRequestService.get(1L)).thenReturn(new SendedItemRequestDto(1L, "Test request", -1L, null, new ArrayList<>()));

        mockMvc.perform(get("/requests/{requestId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test request"));

        verify(itemRequestService, times(1)).get(1L);
    }
}