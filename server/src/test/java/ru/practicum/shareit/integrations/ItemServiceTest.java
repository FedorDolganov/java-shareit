package ru.practicum.shareit.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.item.services.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private BookingRepository bookingRepository;

    private User itemOwner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        itemOwner = userRepository.save(new User(0L, "Test", "test@example.com"));
        otherUser = userRepository.save(new User(0L, "Test 2", "test-2@example.com"));
    }

    @Test
    void addItem_ShouldCreateAndReturnItem() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThat(createdDto.getId()).isGreaterThan(0);
        assertThat(createdDto.getName()).isEqualTo("Test item 1");
        assertThat(createdDto.getDescription()).isEqualTo("Test desc 1");
        assertThat(createdDto.getAvailable()).isTrue();
    }

    @Test
    void addItem_ShouldCreateAndReturnItem_WithRequest() {
        ItemRequestDto request = itemRequestService.add(otherUser.getId(), new ItemRequestDto(0, "Test Request", 0, null));

        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, request.getId()), itemOwner.getId());

        assertThat(createdDto.getId()).isGreaterThan(0);
        assertThat(createdDto.getName()).isEqualTo("Test item 1");
        assertThat(createdDto.getDescription()).isEqualTo("Test desc 1");
        assertThat(createdDto.getAvailable()).isTrue();
        assertThat(createdDto.getRequestId()).isEqualTo(request.getId());
    }

    @Test
    void addItem_ShouldCreateAndReturnItem_WithNotFoundRequest() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, 999L), itemOwner.getId()));
    }

    @Test
    void addItem_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), 999L));
    }

    @Test
    void getItem_ShouldReturnItemWithBookingsAndComments() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        ItemDtoWithBooking foundItem = itemService.getItem(createdDto.getId());

        assertThat(foundItem.getId()).isEqualTo(createdDto.getId());
        assertThat(foundItem.getName()).isEqualTo("Test item 1");
        assertThat(foundItem.getComments()).isEmpty();
        assertThat(foundItem.getNextBooking()).isNull();
        assertThat(foundItem.getLastBooking()).isNull();
    }

    @Test
    void getItem_ShouldReturnItemWithBookingsAndCommentsAndRequestId() {
        ItemRequestDto finalRequest = itemRequestService.add(otherUser.getId(), new ItemRequestDto(0, "TestRequest", 0, null));

        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, finalRequest.getId()), itemOwner.getId());

        SendedBookingDto finalBooking = bookingService.addBooking(new BookingDto(0, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), createdDto.getId(), BookingStatus.APPROVED), otherUser.getId());

        bookingService.updateApproved(itemOwner.getId(), true, finalBooking.getId());


        ItemDtoWithBooking foundItem = itemService.getItem(createdDto.getId());

        assertThat(foundItem.getId()).isEqualTo(createdDto.getId());
        assertThat(foundItem.getName()).isEqualTo("Test item 1");
        assertThat(foundItem.getRequest()).isEqualTo(finalRequest.getId());
        assertThat(foundItem.getComments()).isEmpty();
        assertThat(foundItem.getNextBooking().getId()).isEqualTo(finalBooking.getId());
        assertThat(foundItem.getLastBooking().getId()).isEqualTo(finalBooking.getId());
    }

    @Test
    void getItem_ShouldThrowException_WhenItemNotFound_WhenHasNotBookingAndComment() {
        assertThrows(NotFoundException.class, () -> itemService.getItem(999L));
    }

    @Test
    void getItemsByUser_ShouldReturnUserItems() {
        itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        List<ItemDtoWithBooking> items = itemService.getItemsByUser(itemOwner.getId());

        assertThat(items).hasSize(1);

        assertThat(items.getFirst().getNextBooking()).isNull();
        assertThat(items.getFirst().getLastBooking()).isNull();
        assertThat(items.getFirst().getComments()).isEmpty();
    }

    @Test
    void getItemsByUser_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getItemsByUser(999L));
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() {
        itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        List<ItemDto> foundItems = itemService.searchItems("Test item 1");

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("Test item 1");
    }

    @Test
    void searchItems_ShouldReturnEmptyList_WhenTextIsBlank() {
        List<ItemDto> foundItems = itemService.searchItems("");

        assertThat(foundItems).isEmpty();
    }

    @Test
    void updateItem_ShouldUpdateAndReturnItem() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        ItemDto updatedDto = itemService.updateItem(new ItemDto(0, "Test item 1 Updated", "Updated Test desc 1", false, null), createdDto.getId(), itemOwner.getId());

        assertThat(updatedDto.getId()).isEqualTo(createdDto.getId());
        assertThat(updatedDto.getName()).isEqualTo("Test item 1 Updated");
        assertThat(updatedDto.getDescription()).isEqualTo("Updated Test desc 1");
        assertThat(updatedDto.getAvailable()).isFalse();
    }

    @Test
    void updateItem_ShouldUpdateWithNullFildsAndReturnItem() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        ItemDto updatedDto = itemService.updateItem(new ItemDto(0, null, null, null, null), createdDto.getId(), itemOwner.getId());

        assertThat(updatedDto.getId()).isEqualTo(createdDto.getId());
        assertThat(updatedDto.getName()).isEqualTo("Test item 1");
        assertThat(updatedDto.getDescription()).isEqualTo("Test desc 1");
        assertThat(updatedDto.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ShouldThrowException_WhenUserNotOwner() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThrows(NoPermutationsException.class, () -> itemService.updateItem(new ItemDto(0, "Test item 1 Updated", null, null, null), createdDto.getId(), otherUser.getId()));
    }

    @Test
    void updateItem_ShouldThrowException_WhenUserIdNotFound() {
        ItemDto createdDto = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(new ItemDto(0, "Test item 1 Updated", null, null, null), createdDto.getId(), 999));
    }

    @Test
    void updateItem_ShouldThrowException_WhenItemIdNotFound() {
        itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(new ItemDto(0, "Test item 1 Updated", null, null, null), 999, otherUser.getId()));
    }

    @Test
    void addComment_ShouldCreateAndReturnComment() {
        ItemDto createdItem = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        Item item = itemRepository.findById(createdItem.getId()).orElseThrow();

        Booking booking = new Booking(
                -1,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(2),
                item,
                otherUser,
                BookingStatus.APPROVED
        );

        bookingRepository.save(booking);

        CommentDto createdComment = itemService.addComment(new CommentDto(0, "Comment", 0, null, null), createdItem.getId(), otherUser.getId());

        assertThat(createdComment.getId()).isGreaterThan(0);
        assertThat(createdComment.getText()).isEqualTo("Comment");
        assertThat(createdComment.getAuthorName()).isEqualTo("Test 2");
    }

    @Test
    void addComment_ShouldThrowException_WhenNoBooking() {
        ItemDto createdItem = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThrows(ValidateException.class, () -> itemService.addComment(new CommentDto(0, "Comment", 0, null, null), createdItem.getId(), otherUser.getId()));
    }

    @Test
    void addComment_ShouldThrowException_WhenUserIdNotFound() {
        ItemDto createdItem = itemService.addItem(new ItemDto(0, "Test item 1", "Test desc 1", true, null), itemOwner.getId());

        assertThrows(NotFoundException.class, () -> itemService.addComment(new CommentDto(0, "Comment", 0, null, null), createdItem.getId(), 999));
    }

    @Test
    void addComment_ShouldThrowException_WhenItemIdNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.addComment(new CommentDto(0, "Comment", 0, null, null), 999, otherUser.getId()));
    }
}