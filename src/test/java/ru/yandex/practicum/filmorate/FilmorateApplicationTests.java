package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FilmorateApplicationTests {
	@Test
	void contextLoads() {}
}
class FilmControllerTest {

	private FilmController filmController;

	@BeforeEach
	void setUp() {
		filmController = new FilmController();
	}

	@Test
	void testAddFilm() {
		Film film = new Film(null, "Test Film", "Description", LocalDate.of(2020, 1, 1), 120);
		ResponseEntity<Film> response = filmController.addFilm(film);

		assertEquals(200, response.getStatusCodeValue());
		assertNotNull(response.getBody().getId());
	}

	@Test
	void testUpdateFilmNotFound() {
		Film film = new Film(999L, "Updated Film", "Updated Description", LocalDate.of(2020, 1, 1), 120);
		ResponseEntity<Film> response = filmController.updateFilm(film);

		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	void testGetAllFilms() {
		filmController.addFilm(new Film(null, "Film1", "Desc1", LocalDate.of(2021, 1, 1), 90));
		filmController.addFilm(new Film(null, "Film2", "Desc2", LocalDate.of(2022, 1, 1), 100));

		ResponseEntity<List<Film>> response = filmController.getAllFilms();
		assertEquals(2, response.getBody().size());
	}
}

class UserControllerTest {
	private UserController userController;

	@BeforeEach
	void setUp() {
		userController = new UserController();
	}

	@Test
	void testCreateUser() {
		User user = new User(null, "test@example.com", "testUser", "Test Name", LocalDate.of(1990, 1, 1));
		ResponseEntity<User> response = userController.createUser(user);

		assertEquals(200, response.getStatusCodeValue());
		assertNotNull(response.getBody().getId());
	}

	@Test
	void testUpdateUserNotFound() {
		User user = new User(999L, "updated@example.com", "updatedUser", "Updated Name", LocalDate.of(1990, 1, 1));
		ResponseEntity<User> response = userController.updateUser(user);

		assertEquals(404, response.getStatusCodeValue());
	}

	@Test
	void testGetAllUsers() {
		userController.createUser(new User(null, "user1@example.com", "user1", "User One", LocalDate.of(1985, 5, 5)));
		userController.createUser(new User(null, "user2@example.com", "user2", "User Two", LocalDate.of(1995, 6, 6)));

		ResponseEntity<List<User>> response = userController.getAllUsers();
		assertEquals(2, response.getBody().size());
	}
}

