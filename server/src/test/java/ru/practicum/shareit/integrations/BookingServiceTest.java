package ru.practicum.shareit.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.booking.services.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(new User(0L, "Test", "test@example.com"));
        booker = userRepository.save(new User(0L, "Test 2", "test-2@example.com"));
        item = itemRepository.save(new Item(0L, "Test item 1", "Test desc 1", true, owner, null));
    }

    @Test
    void addBooking_ShouldCreateAndReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        assertThat(createdDto.getId()).isGreaterThan(0);
        assertThat(createdDto.getStart()).isEqualTo(start);
        assertThat(createdDto.getEnd()).isEqualTo(end);
        assertThat(createdDto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(createdDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdDto.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void addBooking_ShouldThrowException_WhenUserNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), 999L));
    }

    @Test
    void addBooking_ShouldThrowException_WhenItemNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(new BookingDto(0, start, end, 999L, null), booker.getId()));
    }

    @Test
    void addBooking_ShouldThrowException_WhenItemNotAvailable() {
        item.setAvailable(false);

        itemRepository.save(item);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertThrows(ValidateException.class, () -> bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId()));
    }

    @Test
    void addBooking_ShouldThrowException_WhenOwnerBooksOwnItem() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertThrows(ValidateException.class, () -> bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), owner.getId()));
    }

    @Test
    void updateApproved_ShouldApproveBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        SendedBookingDto updatedDto = bookingService.updateApproved(owner.getId(), true, createdDto.getId());

        assertThat(updatedDto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void updateApproved_ShouldRejectBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        SendedBookingDto updatedDto = bookingService.updateApproved(owner.getId(), false, createdDto.getId());

        assertThat(updatedDto.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void updateApproved_ShouldThrowException_WhenNotOwner() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        assertThrows(NoPermutationsException.class, () -> bookingService.updateApproved(booker.getId(), true, createdDto.getId()));
    }

    @Test
    void updateApproved_ShouldThrowException_WhenUserIdNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        assertThrows(ValidateException.class, () -> bookingService.updateApproved(999, true, createdDto.getId()));
    }

    @Test
    void updateApproved_ShouldThrowException_WhenBookingIdNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.updateApproved(booker.getId(), true, 999));
    }

    @Test
    void getBooking_ShouldReturnBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        SendedBookingDto foundDto = bookingService.getBooking(booker.getId(), createdDto.getId());

        assertThat(foundDto.getId()).isEqualTo(createdDto.getId());
        assertThat(foundDto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBooking_ShouldThrowException_WhenUserNotOwnerOrBooker() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        User user = userRepository.save(new User(0L, "User", "user@example.com"));

        assertThrows(NoPermutationsException.class, () -> bookingService.getBooking(user.getId(), createdDto.getId()));
    }

    @Test
    void getBooking_ShouldThrowException_WhenBookingIdNotFound() {
        User user = userRepository.save(new User(0L, "User", "user@example.com"));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), 999));
    }

    @Test
    void getBooking_ShouldThrowException_WhenUserIdNotFound() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto createdDto = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(999, createdDto.getId()));
    }

    @Test
    void getUserBookingsByStateAll_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "ALL");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getUserBookingsByStateWaiting_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "WAITING");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getUserBookingsByStateCurrent_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        SendedBookingDto fBooking = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        bookingService.updateApproved(owner.getId(), true, fBooking.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "CURRENT");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getUserBookingsByStatePast_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        LocalDateTime start1 = LocalDateTime.now().plusDays(2);
        LocalDateTime end1 = LocalDateTime.now().plusSeconds(3);

        bookingService.addBooking(new BookingDto(0, start1, end1, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "PAST");

        assertThat(bookings).hasSize(0);
    }

    @Test
    void getUserBookingsByStateFuture_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "FUTURE");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getUserBookingsByStateRejected_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto fBooking = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        bookingService.updateApproved(owner.getId(), false, fBooking.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), "REJECTED");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getUserBookingsByState_ShouldReturnAll_WhenStateIsNull() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getUserBookingsByState(booker.getId(), null);

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getUserBookingsByState_WhenStateNotFound() {
        assertThrows(ValidateException.class, () -> bookingService.getUserBookingsByState(booker.getId(), "randomState"));
    }

    @Test
    void getUserBookingsByState_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.getUserBookingsByState(999, "ALL"));
    }

    @Test
    void getOwnerBookingsByStateAll_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "ALL");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getOwnerBookingsByStateWaiting_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "WAITING");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getOwnerBookingsByStateCurrent_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        SendedBookingDto fBooking = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        bookingService.updateApproved(owner.getId(), true, fBooking.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "CURRENT");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getOwnerBookingsByStatePast_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        LocalDateTime start1 = LocalDateTime.now().plusDays(2);
        LocalDateTime end1 = LocalDateTime.now().plusSeconds(3);

        bookingService.addBooking(new BookingDto(0, start1, end1, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "PAST");

        assertThat(bookings).hasSize(0);
    }

    @Test
    void getOwnerBookingsByStateFuture_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "FUTURE");

        assertThat(bookings).hasSize(1);
    }

    @Test
    void getOwnerBookingsByStateRejected_ShouldReturnBookingsByState() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        SendedBookingDto fBooking = bookingService.addBooking(new BookingDto(0, start, end, item.getId(), null), booker.getId());

        bookingService.updateApproved(owner.getId(), false, fBooking.getId());

        List<SendedBookingDto> bookings = bookingService.getOwnerBookingsByState(owner.getId(), "REJECTED");

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getOwnerBookingsByState_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookingsByState(999, "ALL"));
    }

    @Test
    void getOwnerBookingsByState_WhenStateNotFound() {
        assertThrows(ValidateException .class, () -> bookingService.getOwnerBookingsByState(owner.getId(), "randomState"));
    }
}