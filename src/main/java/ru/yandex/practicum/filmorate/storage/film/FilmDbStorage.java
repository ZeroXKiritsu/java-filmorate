package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundExceptionEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Primary
@Slf4j
@RequiredArgsConstructor
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final LikeStorage likeStorage;
    private final GenreDbStorage genreDbStorage;

    @Override
    public List<Film> findAllFilms() {
        List<Film> films = new ArrayList<>();

        String query = "SELECT film_id, name, description, release_date, duration, rating_mpa_id FROM films";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(query);

        while (filmRows.next()) {
            Film film = buildFilmFromRow(filmRows);
            films.add(film);
        }

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(toMap(film)).intValue());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        genreDbStorage.addGenresForCurrentFilm(film);
        log.info("Поступил запрос на добавление фильма. Фильм добавлен.");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, rating_mpa_id=? WHERE film_id=?";
        int rowsCount = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        mpaDbStorage.addMpaToFilm(film);
        genreDbStorage.updateGenresForCurrentFilm(film);
        genreDbStorage.addGenreNameToFilm(film);
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        if (rowsCount > 0) {
            return film;
        }
        throw new NotFoundExceptionEntity("Фильм не найден.");
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT film_id, name, description, release_date, duration, rating_mpa_id " +
                "FROM films WHERE film_id=?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } catch (RuntimeException e) {
            throw new NotFoundExceptionEntity("Фильм не найден.");
        }
    }

    @Override
    public Film like(int filmId, int userId) {
        Film film = getFilmById(filmId);
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundExceptionEntity("Пользователь не найден.");
        }
        Film film = getFilmById(filmId);
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
        return film;
    }

    @Override
    public List<Film> getRating(int count) {
        String sqlQuery = "SELECT films.*, COUNT(l.film_id) as count FROM films\n" +
                "LEFT JOIN likes l ON films.film_id=l.film_id\n" +
                "GROUP BY films.film_id\n" +
                "ORDER BY count DESC\n" +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(resultSet.getInt("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mpaDbStorage.getMpa(resultSet.getInt("rating_mpa_id")))
                .build();
        film.setLikes(likeStorage.getLikesForCurrentFilm(film.getId()));
        film.setGenres(genreDbStorage.getGenreForCurrentFilm(film.getId()));
        return film;
    }

    private Map<String, Object> toMap(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("rating_mpa_id", film.getMpa().getId());
        return values;
    }

    private Film buildFilmFromRow(SqlRowSet row) {
        int filmId = row.getInt("film_id");
        String name = row.getString("name");
        String description = row.getString("description");
        LocalDate releaseDate = Objects.requireNonNull(row.getDate("release_date")).toLocalDate();
        int duration = row.getInt("duration");
        int ratingMpaId = row.getInt("rating_mpa_id");

        Film film = Film.builder()
            .id(filmId)
            .name(name)
            .description(description)
            .releaseDate(releaseDate)
            .duration(duration)
            .mpa(mpaDbStorage.getMpa(ratingMpaId))
            .build();

        film.setGenres(genreDbStorage.getGenreForCurrentFilm(filmId));
        film.setLikes(likeStorage.getLikesForCurrentFilm(filmId));

        return film;
    }
}
