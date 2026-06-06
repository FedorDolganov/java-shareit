package ru.practicum.shareit.integrations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repositories.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;
import ru.practicum.shareit.request.repositories.ItemRequestRepository;
import ru.practicum.shareit.request.services.ItemRequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestOwner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestOwner = userRepository.save(new User(0L, "Test", "test@example.com"));
        otherUser = userRepository.save(new User(0L, "Test 2", "test-2@example.com"));
    }

    @Test
    void add_ShouldCreateAndReturnRequest() {
        ItemRequestDto createdDto = itemRequestService.add(requestOwner.getId(), new ItemRequestDto(0, "Test request 1", 0, null));

        assertThat(createdDto.getId()).isGreaterThan(0);
        assertThat(createdDto.getDescription()).isEqualTo("Test request 1");
        assertThat(createdDto.getCreated()).isNotNull();
    }

    @Test
    void add_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.add(999L, new ItemRequestDto(0, "Test request 1", 0, null)));
    }

    @Test
    void getByUser_ShouldReturnUserRequestsWithItems() {
        itemRequestService.add(requestOwner.getId(), new ItemRequestDto(0, "Test request 1", 0, null));

        ItemRequestDto createdRequest = itemRequestService.add(requestOwner.getId(), new ItemRequestDto(0, "Test request 2", 0, null));

        itemRepository.save(new Item(
                0L,
                "Test item 1",
                "Test desc 1",
                true,
                requestOwner,
                itemRequestRepository.findById(createdRequest.getId()).orElseThrow()
        ));

        List<SendedItemRequestDto> requests = itemRequestService.getByUser(requestOwner.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getItems()).hasSize(1);
        assertThat(requests.get(0).getItems().get(0).getName()).isEqualTo("Test item 1");
    }

    @Test
    void getByUser_ShouldThrowException_WhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getByUser(999L));
    }

    @Test
    void getAll_ShouldReturnAllRequests() {
        itemRequestService.add(requestOwner.getId(), new ItemRequestDto(0, "Test request 1", 0, null));

        itemRequestService.add(otherUser.getId(), new ItemRequestDto(0, "Test request 2", 0, null));

        List<ItemRequestDto> allRequests = itemRequestService.getAll();

        assertThat(allRequests).hasSize(2);
    }

    @Test
    void get_ShouldReturnRequestWithItems() {
        ItemRequestDto createdRequest = itemRequestService.add(requestOwner.getId(), new ItemRequestDto(0, "Test request 1", 0, null));

        itemRepository.save(new Item(
                0L,
                "Test item 1",
                "Test desc 1",
                true,
                requestOwner,
                itemRequestRepository.findById(createdRequest.getId()).orElseThrow()
        ));

        SendedItemRequestDto foundRequest = itemRequestService.get(createdRequest.getId());

        assertThat(foundRequest.getId()).isEqualTo(createdRequest.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("Test request 1");
        assertThat(foundRequest.getItems()).hasSize(1);
        assertThat(foundRequest.getItems().get(0).getName()).isEqualTo("Test item 1");
    }

    @Test
    void get_ShouldThrowException_WhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.get(999L));
    }
}