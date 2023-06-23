package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exceptions.FilmException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
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

    @GetMapping
    public List<Film> getAllFilms() {
        // Логика получения всех фильмов
        return new ArrayList<>(films.values());
    }

    private int getId() {
        return ++id;
    }

    // Вспомогательный метод для валидации фильма
    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-20"))) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }
    }
}
