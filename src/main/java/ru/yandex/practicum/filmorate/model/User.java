package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;



@Data
@Builder
public class User {

    private int id;
    @NotBlank(message = "логин не должен быть пустым")
    @Email(message = "неверный адрес электронной почты")
    private String email;
    @NonNull
    @NotBlank(message = "логин не должен быть пустым")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}