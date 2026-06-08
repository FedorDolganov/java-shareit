package ru.practicum.shareit.booking.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.SendedBookingDto;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.mappings.BookingMapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Transactional
    @Override
    public SendedBookingDto addBooking(BookingDto booking, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        Optional<Item> item = itemRepository.findById(booking.getItemId());

        if (item.isEmpty()) {
            throw new NotFoundException("ID предмета, который вы хотите забронировать не найден");
        }

        if (!item.get().getAvailable()) {
            throw new ValidateException("Этот предмет недоступен для бронирования");
        }

        if (item.get().getOwner().getId() == userId) {
            throw new ValidateException("Вы не можете забронировать свой же предмет");
        }

        booking.setStatus(BookingStatus.WAITING);

        return BookingMapping.toSended(bookingRepository.save(BookingMapping.to(booking, item.get(), user.get())));
    }

    @Transactional
    @Override
    public SendedBookingDto updateApproved(long userId, boolean approved, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("ID бронирования не найден в базе данных");
        }

        if (!userRepository.existsById(userId)) {
            throw new ValidateException("Используемый вами ID не найден в базе данных");
        }

        if (booking.get().getItem().getOwner().getId() != userId) {
            throw new NoPermutationsException("У вас нет прав на изменение этого бронироания");
        }

        if (approved) {
            booking.get().setStatus(BookingStatus.APPROVED);
        } else {
            booking.get().setStatus(BookingStatus.REJECTED);
        }

        return BookingMapping.toSended(bookingRepository.save(booking.get()));
    }

    @Override
    public SendedBookingDto getBooking(long userId, long bookingId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("ID бронирования не найден в базе данных");
        }

        if (!Arrays.asList(booking.get().getItem().getOwner().getId(), booking.get().getBooker().getId()).contains(userId)) {
            throw new NoPermutationsException("Недостаточно прав на просмотр этого бронирования");
        }

        return BookingMapping.toSended(booking.get());
    }

    @Override
    public List<SendedBookingDto> getUserBookingsByState(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        switch (getState(state)) {
            case ALL:
                return bookingRepository.findAllByBooker_Id(userId, Sort.by("start").ascending()).stream()
                    .map(BookingMapping::toSended)
                    .toList();
            case CURRENT:
                return bookingRepository.findAllByBooker_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case PAST:
                return bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case FUTURE:
                return bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case WAITING:
                return bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case REJECTED:
                return bookingRepository.findAllByBooker_IdAndStatus(userId,BookingStatus.REJECTED, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            default:
                return null;
        }
    }

    @Override
    public List<SendedBookingDto> getOwnerBookingsByState(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        switch (getState(state)) {
            case ALL:
                return bookingRepository.findAllByItem_Owner_Id(userId, Sort.by("start").ascending()).stream()
                    .map(BookingMapping::toSended)
                    .toList();
            case CURRENT:
                return bookingRepository.findAllByItem_Owner_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case PAST:
                return bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case FUTURE:
                return bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case WAITING:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId,BookingStatus.WAITING, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case REJECTED:
                return bookingRepository.findAllByItem_Owner_IdAndStatus(userId,BookingStatus.REJECTED, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            default:
                return null;
        }
    }

    private BookingState getState(String state) {
        if (state == null) {
            return BookingState.ALL;
        }

        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (Exception e) {
            throw new ValidateException("Параметр state имеет недоступное значение");
        }
    }

}
