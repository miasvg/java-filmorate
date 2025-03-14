package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerAndValidTest {
    private UserController userController;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private FilmController filmController;
    private InMemoryFilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
        userController = new UserController(userService);

    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testUser");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setId(1L);

        ResponseEntity<User> response = userController.addUser(user);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void testUpdateUserNotFound() throws NotFoundException {
        User user = new User();
        user.setId(999L);
        user.setEmail("updated@example.com");
        user.setLogin("updatedUser");
        user.setName("Updated Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь с ID 999 не найден", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1985, 5, 5));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 6, 6));

        userController.addUser(user1);
        userController.addUser(user2);

        ResponseEntity<List<User>> response = userController.getAllUsers();
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCreateUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email"); // Некорректный email
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Введенный email не соответствует формату email-адресов!", violation.getMessage());
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin(""); // Пустой логин
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Логин не может быть пустым!", violation.getMessage());
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("valid@example.com");
        user.setLogin("validLogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.now().plusDays(1)); // День рождения в будущем

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Дата рождения не может быть в будущем!", violation.getMessage());
    }
}

