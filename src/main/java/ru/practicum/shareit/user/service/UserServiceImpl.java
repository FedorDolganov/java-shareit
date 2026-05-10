package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dao.ServiceRepository;
import ru.practicum.shareit.exceptions.DublicateException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private ServiceRepository repository;

    @Override
    public UserDto addUser(User user) {
        for (User otherUser : repository.getAllUsers()) {
            if (otherUser.getEmail().equals(user.getEmail())) {
                throw new DublicateException("Данная почта уже была зарегистрирована!");
            }
        }

        return repository.addUser(user);
    }

    @Override
    public UserDto updateUser(UserDto user, long id) {
        if (user.getEmail() != null) {
            for (User otherUser : repository.getAllUsers()) {
                if (otherUser.getEmail().equals(user.getEmail())) {
                    throw new DublicateException("Данная почта уже была зарегистрирована!");
                }
            }
        }

        return repository.updateUser(user, id);
    }

    @Override
    public UserDto getUser(long id) {
        return repository.getUser(id);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }
}
