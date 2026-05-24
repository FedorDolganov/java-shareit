package ru.practicum.shareit.booking.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.AddBookingDto;
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
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public SendedBookingDto addBooking(AddBookingDto booking, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        Optional<Item> item = itemRepository.findById(booking.getItemId());

        if (item.isEmpty()) {
            throw new NotFoundException("Индефикатор предмета не найден");
        }

        if (!item.get().getAvailable()) {
            throw new ValidateException("Этот предмет недоступен для бронирования");
        }

        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidateException("Вы не можете сделать окончание бронирвания в прошлом");
        }

        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidateException("Вы не можете сделать начало бронирвания в прошлом");
        }

        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidateException("Время начала должно быть раньше врмени конца");
        }

        if (item.get().getOwner().getId() == userId) {
            throw new ValidateException("Вы не можете забронировать свой же предмет");
        }

        return BookingMapping.toSended(bookingRepository.save(BookingMapping.to(booking, item.get(), user.get())));
    }

    @Override
    public SendedBookingDto updateApproved(long userId, boolean approved, long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("Индефикатор бронирования не найден");
        }

        if (booking.get().getItem().getOwner().getId() != userId) {
            throw new NoPermutationsException("У вас нет прав на изменение этого бронироания");
        }

        if (!userRepository.existsById(userId)) {
            throw new ValidateException("Индефикатор пользователя не найден");
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
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        Optional<Booking> booking = bookingRepository.findById(bookingId);

        if (booking.isEmpty()) {
            throw new NotFoundException("Индефикатор бронирования не найден");
        }

        if (booking.get().getItem().getOwner().getId() != userId && booking.get().getBooker().getId() != userId) {
            throw new NoPermutationsException("Недостаточно прав на просмотр этого бронирования");
        }

        return BookingMapping.toSended(booking.get());
    }

    @Override
    public List<SendedBookingDto> getUserBookingsByState(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        return switch (getState(state)) {
            case ALL -> bookingRepository.findAllByBooker_Id(userId, Sort.by("start").ascending()).stream()
                    .map(BookingMapping::toSended)
                    .toList();
            case CURRENT ->
                    bookingRepository.findAllByBooker_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case PAST ->
                    bookingRepository.findAllByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case FUTURE ->
                    bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case WAITING ->
                    bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case REJECTED ->
                    bookingRepository.findAllByBooker_IdAndStatus(userId,BookingStatus.REJECTED, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
        };
    }

    @Override
    public List<SendedBookingDto> getOwnerBookingsByState(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        return switch (getState(state)) {
            case ALL -> bookingRepository.findAllByItem_Owner_Id(userId, Sort.by("start").ascending()).stream()
                    .map(BookingMapping::toSended)
                    .toList();
            case CURRENT ->
                    bookingRepository.findAllByItem_Owner_IdAndEndIsAfterAndStartIsBefore(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case PAST ->
                    bookingRepository.findAllByItem_Owner_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case FUTURE ->
                    bookingRepository.findAllByItem_Owner_IdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case WAITING ->
                    bookingRepository.findAllByItem_Owner_IdAndStatus(userId,BookingStatus.WAITING, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
            case REJECTED ->
                    bookingRepository.findAllByItem_Owner_IdAndStatus(userId,BookingStatus.REJECTED, Sort.by("start").ascending()).stream()
                            .map(BookingMapping::toSended)
                            .toList();
        };
    }

    private BookingState getState(String state) {
        if (state == null) {
            return BookingState.ALL;
        }

        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (Exception e){
            throw new ValidateException("Параметр state имеет недоступное значение");
        }
    }

}
