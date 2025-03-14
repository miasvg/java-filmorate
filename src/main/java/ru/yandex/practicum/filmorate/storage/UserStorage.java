package ru.yandex.practicum.filmorate.storage;

import javassist.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user) throws NotFoundException;

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    void deleteUser(Long id);
}