package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
        if (userStorage.findById(id).isEmpty()) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
    }
}