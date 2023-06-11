package ru.yandex.practicum.filmorate.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        // Логика создания пользователя
        validateUser(user);
        log.info("Создание нового пользователя: {}", user);
        return user;
    }

    // Вспомогательный метод для валидации пользователя
    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            String errorMessage = "Некорректный адрес электронной почты";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            String errorMessage = "Логин не может быть пустым и содержать пробелы";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (user.getBirthday() != null) {
            LocalDate currentDate = LocalDate.now();
            LocalDate birthday = LocalDate.parse(user.getBirthday());
            if (birthday.isAfter(currentDate)) {
                String errorMessage = "Дата рождения не может быть в будущем";
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
        }
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @Valid @RequestBody User user) {
        // Логика обновления пользователя
        validateUser(user);
        log.info("Обновление пользователя с id {}: {}", id, user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        // Логика получения всех пользователей
        return new ArrayList<>(); // Возвращаем пустой список в качестве примера
    }
}
