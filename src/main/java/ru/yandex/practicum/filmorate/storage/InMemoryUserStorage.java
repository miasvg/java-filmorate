package ru.yandex.practicum.filmorate.storage;


import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@Component
    public class InMemoryUserStorage implements UserStorage {
        private final Map<Long, User> users = new HashMap<>();
        private long userIdCounter = 1;

        @Override
        public User addUser(User user) {
            user.setId(userIdCounter++);
            users.put(user.getId(), user);
            return user;
        }

        @Override
        public User updateUser(User user) throws NotFoundException {
            Long userId = user.getId();
            if (!users.containsKey(userId)) {
                throw new NotFoundException("Пользователь с ID " + userId + " не найден");
            }
            users.replace(userId, user);
            return user;
        }

    @Override
        public List<User> getAllUsers() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> getUserById(Long id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public void deleteUser(Long id) {
            users.remove(id);
        }
    }

