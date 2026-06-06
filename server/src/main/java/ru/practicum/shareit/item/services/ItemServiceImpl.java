package ru.practicum.shareit.item.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.mappings.CommentMapping;
import ru.practicum.shareit.mappings.ItemMapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDtoWithBooking getItem(long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new NotFoundException("ID предмета, который  вы хотите получить не найден");
        }

        return ItemMapping.fromWithBooking(
                item.get(),
                bookingRepository.findNextBooking(id, LocalDateTime.now()),
                bookingRepository.findLastBooking(id, LocalDateTime.now()),
                commentRepository.findAllByItem_Id(id).stream()
                        .map(CommentMapping::from)
                        .toList()
        );
    }

    @Override
    public List<ItemDtoWithBooking> getItemsByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        List<Item> items = itemRepository.findAllByOwner_Id(userId);

        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .toList();

        List<Booking> nextBookings = bookingRepository.findNextBookings(itemsIds, LocalDateTime.now());

        List<Booking> lastBookings = bookingRepository.findLastBookings(itemsIds, LocalDateTime.now());

        List<Comment> comments = commentRepository.findAllComments(itemsIds);

        return items.stream()
                .map(item -> ItemMapping.fromWithBooking(
                        item,
                        nextBookings.stream().filter(booking -> booking.getItem().getId() == item.getId()).findFirst().orElse(null),
                        lastBookings.stream().filter(booking -> booking.getItem().getId() == item.getId()).findFirst().orElse(null),
                        comments.stream()
                                .filter(comment -> comment.getItem().getId() == item.getId())
                                .map(CommentMapping::from)
                                .toList()
                ))
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.searchItems(text).stream()
                .map(ItemMapping::from)
                .toList();
    }

    @Transactional
    @Override
    public ItemDto addItem(ItemDto item, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        if (item.getRequestId() == null) {
            return ItemMapping.from(itemRepository.save(ItemMapping.to(item, user.get(), null)));
        } else {
            Optional<ItemRequest> request = itemRequestRepository.findById(item.getRequestId());

            if (request.isEmpty()) {
                throw new NotFoundException("Запрос, на который вы хотите ответить, не найден");
            }

            return ItemMapping.from(itemRepository.save(ItemMapping.to(item, user.get(), request.get())));
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto item, long id, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        Optional<Item> dbItem = itemRepository.findById(id);

        if (dbItem.isEmpty()) {
            throw new NotFoundException("ID предмета, который вы хотите обновить не найден");
        }

        if (dbItem.get().getOwner().getId() != userId) {
            throw new NoPermutationsException("Вы не являетесь владельцем этого предмета");
        }

        if (item.getName() != null) {
            dbItem.get().setName(item.getName());
        }

        if (item.getDescription() != null) {
            dbItem.get().setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            dbItem.get().setAvailable(item.getAvailable());
        }

        return ItemMapping.from(itemRepository.save(dbItem.get()));
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto comment, long itemId, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException("ID предмета, на который вы хотите оставить комментарий, не найден");
        }

        if (!bookingRepository.existsByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now())) {
            throw new ValidateException("Вы не бронировали этот товар, так что вы не можете оставить на него отзыв");
        }

        comment.setCreated(LocalDateTime.now());

        comment.setItem(itemId);

        comment.setAuthorName(user.get().getName());

        return CommentMapping.from(commentRepository.save(CommentMapping.to(comment, user.get(), item.get())));
    }
}
