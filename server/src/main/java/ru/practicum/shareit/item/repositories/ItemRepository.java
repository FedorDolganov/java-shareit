package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> searchItems(String text);

    @Query("select i " +
            "from Item i " +
            "where i.request.id in ?1")
    List<Item> findAllItemsByRequestsId(List<Long> requestsIds);

    List<Item> findAllByOwner_Id(long userId);

    List<Item> findAllByRequest_Id(long requestId);

}
