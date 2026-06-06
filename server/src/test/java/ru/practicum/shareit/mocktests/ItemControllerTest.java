package ru.practicum.shareit.mocktests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.services.ItemService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void getByUser_ShouldReturnUserItems() throws Exception {
        when(itemService.getItemsByUser(1L)).thenReturn(Arrays.asList(
                new ItemDtoWithBooking(1L, "Test item 1", "Test desc 1", true, null, null, null, null),
                new ItemDtoWithBooking(2L, "Test item 2", "Test desc 2", false, null, null, null, null)
        ));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test item 2"));

        verify(itemService, times(1)).getItemsByUser(1L);
    }

    @Test
    void get_ShouldReturnItemById() throws Exception {
        when(itemService.getItem(1L)).thenReturn(new ItemDtoWithBooking(1L, "Test item 1", "Test desc 1", true, null, null, null, null));

        mockMvc.perform(get("/items/{itemId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test item 1"))
                .andExpect(jsonPath("$.description").value("Test desc 1"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).getItem(1L);
    }

    @Test
    void add_ShouldCreateAndReturnItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class), eq(1L))).thenReturn(new ItemDto(1L, "Test item 1", "Test desc 1", true, null));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto(-1L, "Test item 1", "Test desc 1", true, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test item 1"))
                .andExpect(jsonPath("$.description").value("Test desc 1"))
                .andExpect(jsonPath("$.available").value(true));

        verify(itemService, times(1)).addItem(any(ItemDto.class), eq(1L));
    }

    @Test
    void addComment_ShouldCreateAndReturnComment() throws Exception {
        when(itemService.addComment(any(CommentDto.class), eq(1L), eq(1L))).thenReturn(new CommentDto(1L, "Бе-бе-бе", -1L, "Test", null));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentDto(-1L, "Бе-бе-бе", -1L, null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Бе-бе-бе"))
                .andExpect(jsonPath("$.authorName").value("Test"));

        verify(itemService, times(1)).addComment(any(CommentDto.class), eq(1L), eq(1L));
    }

    @Test
    void update_ShouldUpdateAndReturnItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), eq(1L), eq(1L))).thenReturn(new ItemDto(1L, "Test item 1 Updated", "Updated Test desc 1", false, null));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ItemDto(-1L, "Test item 1 Updated", "Updated Test desc 1", false, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test item 1 Updated"))
                .andExpect(jsonPath("$.description").value("Updated Test desc 1"))
                .andExpect(jsonPath("$.available").value(false));

        verify(itemService, times(1)).updateItem(any(ItemDto.class), eq(1L), eq(1L));
    }

    @Test
    void search_ShouldReturnFoundItems() throws Exception {
        when(itemService.searchItems("Test item 1")).thenReturn(
                Arrays.asList(
                        new ItemDto(1L, "Test item 1", "Test desc 1", true, null),
                        new ItemDto(2L, "Test item 1 blebebe", "Set of Test item 1 blebebe", true, null)
                )
        );

        mockMvc.perform(get("/items/search")
                        .param("text", "Test item 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test item 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test item 1 blebebe"));

        verify(itemService, times(1)).searchItems("Test item 1");
    }
}