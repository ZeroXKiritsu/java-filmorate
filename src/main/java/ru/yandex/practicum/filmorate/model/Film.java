package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
public class Film {

    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @Past
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Integer> likes;
    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;
}