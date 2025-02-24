package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<User> addUser(@RequestBody final User user) {
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
    public ResponseEntity<User> updateUser(@RequestBody final User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя: {}", user);
            return ResponseEntity.notFound().build();
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
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }
}
