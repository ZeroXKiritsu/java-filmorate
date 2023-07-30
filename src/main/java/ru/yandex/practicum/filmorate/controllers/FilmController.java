package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/films", produces = "application/json")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на добавление фильма");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film changeFilm(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на изменения фильма");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{filmId}")
    public void like(@PathVariable String id, @PathVariable String filmId) {
        log.info("Поступил запрос на присвоение лайка фильму.");
        filmService.like(Integer.parseInt(id), Integer.parseInt(filmId));
    }

    @GetMapping()
    public List<Film> getFilms() {
        log.info("Поступил запрос на получение списка всех фильмов.");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable String id) {
        log.info("Получен GET-запрос на получение фильма");
        return filmService.getFilmById(Integer.parseInt(id));
    }

    @GetMapping("/popular")
    public List<Film> getBestFilms(@RequestParam(defaultValue = "10") String count) {
        log.info("Поступил запрос на получение списка популярных фильмов.");
        return filmService.getTopFilms(Integer.parseInt(count));
    }

    @DeleteMapping("/{id}/like/{filmId}")
    public void deleteLike(@PathVariable String id, @PathVariable String filmId) {
        log.info("Поступил запрос на удаление лайка у фильма.");
        filmService.deleteLike(Integer.parseInt(filmId), Integer.parseInt(id));
    }
}
