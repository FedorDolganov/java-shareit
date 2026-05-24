package ru.practicum.shareit.item.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repositories.BookingRepository;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.repositories.CommentRepository;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.mappings.CommentMapping;
import ru.practicum.shareit.mappings.ItemMapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;

    @Override
    public ItemDtoWithBooking getItem(long id) {
        Optional<Item> item = itemRepository.findById(id);

        if (item.isEmpty()) {
            throw new NotFoundException("Предмет с данным индефикатором не найден");
        }

        return ItemMapping.fromWithBooking(
                item.get(),
                bookingRepository.findNextBooking(id, LocalDateTime.now()),
                bookingRepository.findNextBooking(id, LocalDateTime.now()),
                commentRepository.findAllByItem_Id(id).stream()
                        .map(CommentMapping::from)
                        .toList()
        );
    }

    @Override
    public List<ItemDtoWithBooking> getItemsByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        return itemRepository.findAllByOwner_Id(userId).stream()
                .map(item -> ItemMapping.fromWithBooking(
                        item,
                        bookingRepository.findNextBooking(item.getId(), LocalDateTime.now()),
                        bookingRepository.findNextBooking(item.getId(), LocalDateTime.now()),
                        commentRepository.findAllByItem_Id(item.getId()).stream()
                                .map(CommentMapping::from)
                                .toList())
                )
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

    @Override
    public ItemDto addItem(AddItemDto item, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        return ItemMapping.from(itemRepository.save(ItemMapping.to(item, user.get())));
    }

    @Override
    public ItemDto updateItem(ItemDto item, long id, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        Optional<Item> dbItem = itemRepository.findById(id);

        if (dbItem.isEmpty()) {
            throw new NotFoundException("Индефикатор предмета не найден");
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

    @Override
    public CommentDto addComment(CommentDto comment, long itemId, long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        Optional<Item> item = itemRepository.findById(itemId);

        if (item.isEmpty()) {
            throw new NotFoundException("Индефикатор предмета не найден");
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
