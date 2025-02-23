package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private long userIdCounter = 1;

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        user.setId(userIdCounter++);
        users.put(user.getId(), user);
        log.info("Создан пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Попытка обновить несуществующего пользователя: {}", user);
            return ResponseEntity.notFound().build();
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }
}