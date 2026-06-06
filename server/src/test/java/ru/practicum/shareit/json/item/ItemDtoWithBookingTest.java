package ru.practicum.shareit.json.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingTest {

    @Autowired
    private JacksonTester<ItemDtoWithBooking> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto booking = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 1L,  BookingStatus.APPROVED);

        ItemDtoWithBooking itemDto = new ItemDtoWithBooking(1L, "Test item 1", "Test desc 1", true, 1L,
                List.of(
                        new CommentDto(1L, "Comment", 1L, "Test", LocalDateTime.now().plusDays(1))
                ), booking, booking
        );

        JsonContent<ItemDtoWithBooking> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test item 1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test desc 1");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Comment");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
    }
}