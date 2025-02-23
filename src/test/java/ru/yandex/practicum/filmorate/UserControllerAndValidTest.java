package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerAndValidTest {
    UserController userController;

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
        ResponseEntity<User> response = userController.updateUser(user);

        assertEquals(404, response.getStatusCodeValue());
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
        User user = new User(null, "invalid-email", "user123", "User Name", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.addUser(user));
        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    void testCreateUserWithEmptyLogin() {
        User user = new User(null, "test@example.com", "", "User Name", LocalDate.of(1990, 1, 1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.addUser(user));
        assertEquals("Login cannot be empty or contain spaces", exception.getMessage());
    }

    @Test
    void testCreateUserWithFutureBirthday() {
        User user = new User(null, "test@example.com", "user123", "User Name", LocalDate.of(2100, 1, 1));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.addUser(user));
        assertEquals("Birthday cannot be in the future", exception.getMessage());
    }
}