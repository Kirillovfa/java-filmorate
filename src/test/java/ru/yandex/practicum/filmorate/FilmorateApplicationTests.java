package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmorateApplicationTests {
	private InMemoryUserStorage userStorage;
	private InMemoryFilmStorage filmStorage;
	private UserService userService;
	private FilmService filmService;

	@BeforeEach
	void setUp() {
		userStorage = new InMemoryUserStorage();
		filmStorage = new InMemoryFilmStorage();
		userService = new UserService(userStorage);
		filmService = new FilmService(filmStorage, userStorage);
	}

	@Test
	void testCreateAndGetUser() {
		User user = new User();
		user.setEmail("test@mail.ru");
		user.setLogin("логин");
		user.setName("Тестовый пользователь");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User created = userStorage.add(user);

		assertTrue(created.getId() > 0);
		assertEquals(user.getLogin(), userStorage.findById(created.getId()).get().getLogin());
	}

	@Test
	void testCreateAndGetFilm() {
		Film film = new Film();
		film.setName("Тестовый фильм");
		film.setDescription("Описание фильма");
		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(120);
		Film created = filmStorage.add(film);

		assertTrue(created.getId() > 0);
		assertEquals(film.getName(), filmStorage.findById(created.getId()).get().getName());
	}

	@Test
	void testAddAndRemoveFriend() {
		User user1 = new User();
		user1.setEmail("первый@mail.ru");
		user1.setLogin("первый");
		user1.setName("Первый Пользователь");
		user1.setBirthday(LocalDate.of(1990, 1, 1));
		userStorage.add(user1);

		User user2 = new User();
		user2.setEmail("второй@mail.ru");
		user2.setLogin("второй");
		user2.setName("Второй Пользователь");
		user2.setBirthday(LocalDate.of(1991, 2, 2));
		userStorage.add(user2);

		userService.addFriend(user1.getId(), user2.getId());
		assertTrue(userService.getFriends(user1.getId()).stream().anyMatch(u -> u.getId() == user2.getId()));
		assertTrue(userService.getFriends(user2.getId()).stream().anyMatch(u -> u.getId() == user1.getId()));

		userService.removeFriend(user1.getId(), user2.getId());
		assertFalse(userService.getFriends(user1.getId()).stream().anyMatch(u -> u.getId() == user2.getId()));
		assertFalse(userService.getFriends(user2.getId()).stream().anyMatch(u -> u.getId() == user1.getId()));
	}

	@Test
	void testCommonFriends() {
		User user1 = new User();
		user1.setEmail("один@mail.ru");
		user1.setLogin("один");
		user1.setName("Один");
		user1.setBirthday(LocalDate.of(1990, 1, 1));
		userStorage.add(user1);

		User user2 = new User();
		user2.setEmail("два@mail.ru");
		user2.setLogin("два");
		user2.setName("Два");
		user2.setBirthday(LocalDate.of(1991, 1, 1));
		userStorage.add(user2);

		User user3 = new User();
		user3.setEmail("три@mail.ru");
		user3.setLogin("три");
		user3.setName("Три");
		user3.setBirthday(LocalDate.of(1992, 1, 1));
		userStorage.add(user3);

		userService.addFriend(user1.getId(), user3.getId());
		userService.addFriend(user2.getId(), user3.getId());

		List<User> common = userService.getCommonFriends(user1.getId(), user2.getId());
		assertEquals(1, common.size());
		assertEquals(user3.getId(), common.get(0).getId());
	}

	@Test
	void testLikeAndPopularFilms() {
		User user = new User();
		user.setEmail("почта@mail.ru");
		user.setLogin("лайк");
		user.setName("Лайк Пользователь");
		user.setBirthday(LocalDate.of(1990, 1, 1));
		userStorage.add(user);

		Film film1 = new Film();
		film1.setName("Фильм Первый");
		film1.setDescription("Описание первого фильма");
		film1.setReleaseDate(LocalDate.of(2019, 1, 1));
		film1.setDuration(100);
		filmStorage.add(film1);

		Film film2 = new Film();
		film2.setName("Фильм Второй");
		film2.setDescription("Описание второго фильма");
		film2.setReleaseDate(LocalDate.of(2018, 1, 1));
		film2.setDuration(90);
		filmStorage.add(film2);

		filmService.addLike(film2.getId(), user.getId());
		filmService.addLike(film1.getId(), user.getId());

		List<Film> popular = filmService.getPopular(1);
		assertEquals(1, popular.size());
		assertTrue(popular.get(0).getLikes().size() > 0);
	}
}