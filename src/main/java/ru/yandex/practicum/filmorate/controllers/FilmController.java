package ru.yandex.practicum.filmorate.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        // Логика добавления фильма
        validateFilm(film);
        log.info("Добавление нового фильма: {}", film);
        return film;
    }

    // Вспомогательный метод для валидации фильма
    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            String errorMessage = "Название фильма не может быть пустым";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            String errorMessage = "Максимальная длина описания фильма — 200 символов";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        // Проверяем, что дата релиза не раньше 28 декабря 1895 года
        LocalDate minReleaseDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate = LocalDate.parse(film.getReleaseDate());
        if (releaseDate.isBefore(minReleaseDate)) {
            String errorMessage = "Дата релиза должна быть не раньше 28 декабря 1895 года";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (film.getDuration() <= 0) {
            String errorMessage = "Продолжительность фильма должна быть положительной";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable int id, @Valid @RequestBody Film film) {
        // Логика обновления фильма
        validateFilm(film);
        log.info("Обновление фильма с id {}: {}", id, film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        // Логика получения всех фильмов
        return new ArrayList<>(); // Возвращаем пустой список в качестве примера
    }
}
