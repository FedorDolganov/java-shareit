package ru.practicum.shareit.mappings;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

public class CommentMapping {

    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment to(CommentDto comment, User user, Item item) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                item,
                user,
                comment.getCreated()
        );
    }

}
