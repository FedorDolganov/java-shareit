package ru.practicum.shareit.item.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(long id);

    @Query("select c " +
            "from Comment c " +
            "where c.item.id in ?1")
    List<Comment> findAllComments(List<Long> itemIds);

}
