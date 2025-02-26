package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerAndValidTest {
    UserController userController;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testCreateUser() {
        User user = new User(null, "test@example.com", "testUser", "Test Name", LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = userController.addUser(user);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void testUpdateUserNotFound() {
        User user = new User(999L, "updated@example.com", "updatedUser", "Updated Name", LocalDate.of(1990, 1, 1));
        ResponseEntity<?> response = userController.updateUser(user);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Пользователь с ID 999 не найден.", errorResponse.get("error"));
    }

    @Test
    void testGetAllUsers() {
        userController.addUser(new User(null, "user1@example.com", "user1", "User One", LocalDate.of(1985, 5, 5)));
        userController.addUser(new User(null, "user2@example.com", "user2", "User Two", LocalDate.of(1995, 6, 6)));
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
        assertEquals("Логин не может содержать пробелы!", violation.getMessage());
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