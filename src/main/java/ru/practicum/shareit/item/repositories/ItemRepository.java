package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Component
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> searchItems(String text);

    List<Item> findAllByOwner_Id(long userId);

}
