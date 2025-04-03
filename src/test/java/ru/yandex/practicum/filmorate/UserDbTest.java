package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class UserDbTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("user@mail.com");
        testUser.setLogin("userLogin");
        testUser.setName("User Name");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testAddUser() {
        User addedUser = userStorage.addUser(testUser);

        assertThat(addedUser.getId()).isNotNull();
        assertThat(addedUser.getEmail()).isEqualTo(testUser.getEmail());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                addedUser.getId()
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testUpdateUser() {
        User addedUser = userStorage.addUser(testUser);
        addedUser.setName("Updated Name");

        User updatedUser = userStorage.updateUser(addedUser);

        assertThat(updatedUser.getName()).isEqualTo("Updated Name");

        String name = jdbcTemplate.queryForObject(
                "SELECT name FROM users WHERE id = ?",
                String.class,
                addedUser.getId()
        );
        assertThat(name).isEqualTo("Updated Name");
    }


    @Test
    void testGetUserById() {
        User addedUser = userStorage.addUser(testUser);
        Optional<User> foundUser = userStorage.getUserById(addedUser.getId());

        assertThat(foundUser)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(addedUser);
    }

    @Test
    void testGetAllUsers() {
        userStorage.addUser(testUser);
        User anotherUser = new User();
        anotherUser.setEmail("another@mail.com");
        anotherUser.setLogin("anotherLogin");
        anotherUser.setBirthday(LocalDate.of(1995, 5, 5));
        userStorage.addUser(anotherUser);

        List<User> users = userStorage.getAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.addUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@mail.com");
        user2.setLogin("friendLogin");
        user2.setBirthday(LocalDate.of(1995, 5, 5)); // Добавляем дату рождения
        user2 = userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                user1.getId(), user2.getId()
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testRemoveFriend() {
        User user1 = userStorage.addUser(testUser);

        User user2 = new User();
        user2.setEmail("friend@mail.com");
        user2.setLogin("friendLogin");
        user2.setBirthday(LocalDate.of(1995, 5, 5)); // Добавляем дату рождения
        user2 = userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.removeFriend(user1.getId(), user2.getId());

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                user1.getId(), user2.getId()
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testGetFriends() {
        // Создаем первого пользователя (уже есть в setUp)
        User user1 = userStorage.addUser(testUser);

        // Создаем второго пользователя с обязательными полями
        User user2 = new User();
        user2.setEmail("friend@mail.com");
        user2.setLogin("friendLogin");
        user2.setBirthday(LocalDate.of(1995, 5, 5)); // Добавляем дату рождения
        user2 = userStorage.addUser(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());
        assertThat(friends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2.getId());
    }
}
