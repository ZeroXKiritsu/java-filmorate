package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Data
@Builder
public class Film {

    private int id;
    @NotBlank(message = "имя не должно быть пустым")
    private String name;
    @Size(max = 200)
    private String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "продолжительность не должна быть отрицательной")
    private int duration;

}