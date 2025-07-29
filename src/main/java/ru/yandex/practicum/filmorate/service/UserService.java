package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        validate(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        validate(user);
        if (userStorage.findById(user.getId()).isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        return userStorage.update(user);
    }

    public User addFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public User removeFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        return user;
    }

    public List<User> getFriends(int userId) {
        User user = getUserOrThrow(userId);
        return user.getFriendIds().stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        Set<Integer> commonIds = new HashSet<>(user.getFriendIds());
        commonIds.retainAll(other.getFriendIds());

        return commonIds.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public User getUserOrThrow(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + id));
    }

    private void validate(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email некорректный");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }
}