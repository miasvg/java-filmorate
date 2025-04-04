package ru.yandex.practicum.filmorate.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.Optional;
import java.util.List;


@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserDbStorage userDbStorage) {
        this.userStorage = userStorage;
        this.userDbStorage = userDbStorage;
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin()); // Если имя не указано, используем логин
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws NotFoundException {
        // Проверяем существование пользователя
        if (!userStorage.getUserById(user.getId()).isPresent()) {
            throw new NotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) throws NotFoundException {
        if (!userStorage.getUserById(userId).isPresent()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        if (!userStorage.getUserById(friendId).isPresent()) {
            throw new NotFoundException("User with id " + friendId + " not found");
        }
        userDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) throws NotFoundException {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        if (userStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("User with id " + friendId + " not found");
        }
        userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userDbStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userStorage.getUserById(otherId).orElseThrow(() -> new RuntimeException("Other user not found"));
        return userDbStorage.getCommonFriends(userId, otherId);

    }
}
