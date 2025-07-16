package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private int id;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 1, max = 200)
    private String name;

    private String description;
    private LocalDate releaseDate;
    private int duration;
}