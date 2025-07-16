package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	@Test
	void filmValidationTest() {
		FilmController filmController = new FilmController();

		// Граничные условия — корректный фильм
		Film goodFilm = new Film();
		goodFilm.setName("Фильм");
		goodFilm.setDescription("Описание");
		goodFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
		goodFilm.setDuration(120);
		assertDoesNotThrow(() -> filmController.addFilm(goodFilm));

		// Пустое название
		Film noName = new Film();
		noName.setName("");
		noName.setDescription("Описание");
		noName.setReleaseDate(LocalDate.of(2000, 1, 1));
		noName.setDuration(100);
		assertThrows(ValidationException.class, () -> filmController.addFilm(noName));

		// Дата релиза до 28 декабря 1895 года
		Film oldFilm = new Film();
		oldFilm.setName("Фильм");
		oldFilm.setDescription("Описание");
		oldFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
		oldFilm.setDuration(100);
		assertThrows(ValidationException.class, () -> filmController.addFilm(oldFilm));

		// Продолжительность <= 0
		Film zeroDuration = new Film();
		zeroDuration.setName("Фильм");
		zeroDuration.setDescription("Описание");
		zeroDuration.setReleaseDate(LocalDate.of(2000, 1, 1));
		zeroDuration.setDuration(0);
		assertThrows(ValidationException.class, () -> filmController.addFilm(zeroDuration));
	}

	@Test
	void userValidationTest() {
		UserController userController = new UserController();

		// Корректный пользователь
		User goodUser = new User();
		goodUser.setEmail("user@mail.com");
		goodUser.setLogin("login");
		goodUser.setName("Имя");
		goodUser.setBirthday(LocalDate.of(2000, 1, 1));
		assertDoesNotThrow(() -> userController.createUser(goodUser));

		// Пустой email
		User noEmail = new User();
		noEmail.setEmail("");
		noEmail.setLogin("login");
		noEmail.setName("Имя");
		noEmail.setBirthday(LocalDate.of(2000, 1, 1));
		assertThrows(ValidationException.class, () -> userController.createUser(noEmail));

		// Пустой логин
		User noLogin = new User();
		noLogin.setEmail("user@mail.com");
		noLogin.setLogin("");
		noLogin.setName("Имя");
		noLogin.setBirthday(LocalDate.of(2000, 1, 1));
		assertThrows(ValidationException.class, () -> userController.createUser(noLogin));

		// Будущая дата рождения
		User futureBirthday = new User();
		futureBirthday.setEmail("user@mail.com");
		futureBirthday.setLogin("login");
		futureBirthday.setName("Имя");
		futureBirthday.setBirthday(LocalDate.now().plusDays(1));
		assertThrows(ValidationException.class, () -> userController.createUser(futureBirthday));
	}
}