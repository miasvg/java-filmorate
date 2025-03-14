package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Controller for managing Users in the Filmorate application.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    /** A map to store users with their IDs. */
    private final Map<Long, User> users = new HashMap<>();
    /** Couner for users Ids.*/
    private long userIdCounter = 1;

    /**
     * Adds new user.
     *
     * @param user the user to add.
     * @return created user.
      */
    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        getDisplayName(user);
        user.setName(getDisplayName(user));
        user.setId(userIdCounter++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    /**
     * Updates an existing film in the collection.
     *
     * @param user the user to update.
     * @return the updated user if found, otherwise a 404 response.
     */
    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя: {}", user);
            // Возвращаем JSON с сообщением об ошибке
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Пользователь с ID " + user.getId() + " не найден.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    /**
     * Retrieves all users in the collection.
     *
     * @return a list of all films.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> userList = new ArrayList<>(users.values());
        userList.forEach(user -> user.setName(getDisplayName(user)));
        return ResponseEntity.ok(userList);
    }

    private String getDisplayName(User user) {
        return (user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName();
    }
}
