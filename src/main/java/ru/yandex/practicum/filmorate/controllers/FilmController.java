package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exceptions.FilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/films", produces = "application/json")
@Validated
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        // Логика добавления фильма
        validateFilm(film);
        film.setId(getId());
        films.put(film.getId(), film);
        log.info("Добавление нового фильма: {}", film);
        return film;
    }

    // Вспомогательный метод для валидации фильма
    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        // Логика обновления фильма
        if (films.get(film.getId()) != null) {
            validateFilm(film);
            films.put(film.getId(), film);
            log.info("Обновление фильма {}", film);
        } else {
            log.error("Фильм не найден.");
            throw new FilmException("Film not found.");
        }

        return film;
    }

    private int getId() {
        return ++id;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        // Логика получения всех фильмов
        return new ArrayList<>(films.values());
    }
}
