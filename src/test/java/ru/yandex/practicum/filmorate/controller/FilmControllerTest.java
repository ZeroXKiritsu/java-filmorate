package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

public class FilmControllerTest {

    FilmStorage filmStorage;
    FilmController controller;

    FilmService filmService;
    Film testFilm;

    @BeforeEach
    protected void init() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
        controller = new FilmController(filmService);
        testFilm = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание тестового фильма")
                .releaseDate(LocalDate.of(1999, 12,27))
                .duration(87)
                .build();

    }

    @Test
    void createFilm_NameIsBlank_badRequestTest() {
        testFilm.setName("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(testFilm), "Некорректно указано название фильма.");
    }

    @Test
    void createFilm_IncorrectDescription_badRequestTest() {
        testFilm.setDescription("a".repeat(201));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(testFilm), "Превышено количество символов в описании фильма.");
    }

    @Test
    void createFilm_RealiseDateInFuture_badRequestTest() {
        testFilm.setReleaseDate(LocalDate.of(2033, 4, 14));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(testFilm), "Некорректно указана дата релиза.");
    }

    @Test
    void createFilm_RealiseDateBeforeFirstFilmDate_badRequestTest() {
        testFilm.setReleaseDate(LocalDate.of(1833, 4, 14));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(testFilm), "Некорректно указана дата релиза.");
    }

    @Test
    void createNewCorrectFilm_isOkTest() {
       controller.create(testFilm);
       Assertions.assertEquals(testFilm, filmStorage.getFilmById(1));
    }

}