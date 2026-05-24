package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DublicateException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.mappings.UserMapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.AddUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Override
    public UserDto addUser(AddUserDto user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new DublicateException("Данная почта уже была зарегистрирована!");
        }

        User finalUser = repository.save(UserMapping.to(user));

        return UserMapping.from(finalUser);
    }

    @Override
    public UserDto updateUser(UserDto user, long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Индефикатор пользователя не найден");
        }

        if (user.getEmail() != null) {
            if (repository.existsByEmail(user.getEmail())) {
                throw new DublicateException("Данная почта уже была зарегистрирована!");
            }
        }

        User userRef = repository.getReferenceById(id);

        if (user.getName() != null) {
            userRef.setName(user.getName());
        } if (user.getEmail() != null) {
            userRef.setEmail(user.getEmail());
        }

        return UserMapping.from(repository.save(userRef));
    }

    @Override
    public UserDto getUser(long id) {
        Optional<User> user = repository.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с данным индефикатором не найден");
        }

        return UserMapping.from(user.get());
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
