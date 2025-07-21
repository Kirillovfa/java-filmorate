package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        validate(film);
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        validate(film);
        if (filmStorage.findById(film.getId()).isEmpty()) {
            throw new NoSuchElementException("Film not found");
        }
        return filmStorage.update(film);
    }

    public Film addLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        film.getLikes().add(userId);
        filmStorage.update(film);

        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = getFilmOrThrow(filmId);
        checkUserExists(userId);

        film.getLikes().remove(userId);
        filmStorage.update(film);

        return film;
    }

    public List<Film> getPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmOrThrow(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Film not found with id: " + id));
    }

    private void checkUserExists(int id) {
        if (userStorage.findAll().contains(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}