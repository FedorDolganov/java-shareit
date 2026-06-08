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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void getUsers() throws Exception {
        when(itemClient.getItemsByUser(eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(new ItemDto())
        );

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItemsByUser(eq(1L));
    }

    @Test
    void getById() throws Exception {
        when(itemClient.getItem(eq(1L), eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body("[]")
        );

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).getItem(eq(1L), eq(1L));
    }

    @Test
    void addItem() throws Exception {
        ItemDto dto = new ItemDto("Test", "test", true, 1L);

        when(itemClient.addItem(eq(1L), any(ItemDto.class))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addItem(eq(1L), any(ItemDto.class));
    }

    @Test
    void addComment() throws Exception {
        CommentDto dto = new CommentDto("Test");

        when(itemClient.addComment(eq(1L), any(CommentDto.class), eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).addComment(eq(1L), any(CommentDto.class), eq(1L));
    }

    @Test
    void update() throws Exception {
        ItemDto dto = new ItemDto("Test", "test", true, 1L);

        when(itemClient.updateItem(eq(1L), any(ItemDto.class), eq(1L))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(dto)
        );

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).updateItem(eq(1L), any(ItemDto.class), eq(1L));
    }

    @Test
    void search() throws Exception {
        when(itemClient.searchItems(eq(1L), eq("test"))).thenReturn(
                ResponseEntity
                        .status(HttpStatus.OK)
                        .header("X-Sharer-User-Id", "1")
                        .body(new ItemDto())
        );

        mockMvc.perform(get("/items/search", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test"))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).searchItems(eq(1L), eq("test"));
    }
}