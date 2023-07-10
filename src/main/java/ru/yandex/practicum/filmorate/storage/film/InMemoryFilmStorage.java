package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundExceptionEntity;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int id;

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setLikes(new HashSet<>());
        film.setId(getId());
        films.put(film.getId(), film);
        log.info("Добавление нового фильма: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.get(film.getId()) != null) {
            validateFilm(film);
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            log.info("Обновление фильма {}", film);
        } else {
            log.error("Фильм не найден.");
            throw new NotFoundExceptionEntity("Film not found.");
        }

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundExceptionEntity("Film not found.");
        }
    }

    public int getId() {
        return ++id;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))
                || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректно указана дата релиза.");
        }

        if (film.getName().isEmpty()) {
            throw new ValidationException("Некорректно указано название фильма.");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Превышено количество символов в описании фильма.");
        }
    }
}