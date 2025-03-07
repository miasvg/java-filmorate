package ru.yandex.practicum.filmorate.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws NotFoundException {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userStorage.getUserById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            userStorage.getUserById(friendId).ifPresent(friends::add);
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userStorage.getUserById(otherId).orElseThrow(() -> new RuntimeException("Other user not found"));
        Set<Long> commonFriends = new HashSet<>(user.getFriends());
        commonFriends.retainAll(otherUser.getFriends());
        List<User> commonFriendsList = new ArrayList<>();
        for (Long friendId : commonFriends) {
            userStorage.getUserById(friendId).ifPresent(commonFriendsList::add);
        }
        return commonFriendsList;
    }
}
