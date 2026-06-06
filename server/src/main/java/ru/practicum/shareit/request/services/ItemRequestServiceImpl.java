package ru.practicum.shareit.request.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.mappings.ItemMapping;
import ru.practicum.shareit.mappings.ItemRequestMapping;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private ItemRequestRepository itemRequestRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Override
    public ItemRequestDto add(long userId, ItemRequestDto request) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        request.setCreated(LocalDateTime.now());

        return ItemRequestMapping.to(itemRequestRepository.save(ItemRequestMapping.from(request, user.get())));
    }

    @Override
    public List<SendedItemRequestDto> getByUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Используемый вами ID не найден в базе данных");
        }

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_Id(userId, Sort.by(Sort.Direction.DESC, "created"));

        List<Item> items = itemRepository.findAllItemsByRequestsId(requests.stream()
                .map(ItemRequest::getId)
                .toList());

        return requests.stream()
                .map(itemRequest -> ItemRequestMapping.toSended(itemRequest, items.stream()
                        .filter(item -> item.getRequest().getId() == itemRequest.getId())
                        .map(ItemMapping::from)
                        .toList())
                )
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return itemRequestRepository.findAll(Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(ItemRequestMapping::to)
                .toList();
    }

    @Override
    public SendedItemRequestDto get(long requestId) {
        Optional<ItemRequest> request = itemRequestRepository.findById(requestId);

        if (request.isEmpty()) {
            throw new NotFoundException("Запрос по указанному ID не найден");
        }

        return ItemRequestMapping.toSended(request.get(), itemRepository.findAllByRequest_Id(requestId).stream()
                .map(ItemMapping::from)
                .toList());
    }

}
