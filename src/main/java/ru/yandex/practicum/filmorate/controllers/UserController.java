package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exceptions.UserException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/users", produces = "application/json")
@Validated
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        // Логика создания пользователя
        validateUser(user);
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Создание нового пользователя: {}", user);
        return user;
    }

    // Вспомогательный метод для валидации пользователя
    private void validateUser(User user)  throws ValidationException {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        // Логика обновления пользователя
        if (users.get(user.getId()) != null) {
            validateUser(user);
            users.put(user.getId(), user);
            log.info("Обновление пользователя с id {}: {}", id, user);
        } else {
            log.error("Пользователь не найден.");
            throw new UserException("User not found.");
        }

        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        // Логика получения всех пользователей
        return new ArrayList<>(users.values());
    }

    private int getId() {
        return ++id;
    }
}
