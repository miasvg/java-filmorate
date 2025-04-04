package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
public class FilmDbTest {
    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreStorage;
    private final JdbcTemplate jdbcTemplate;
    private Film testFilm;

    @BeforeEach
    void setUp() {

        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM film_likes");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM genres");
        jdbcTemplate.execute("DELETE FROM mpa_ratings");
        // Инициализация тестовых данных
        jdbcTemplate.update("INSERT INTO mpa_ratings (id, name) VALUES (1, 'G')");
        jdbcTemplate.update("INSERT INTO genres (id, name) VALUES (1, 'Комедия')");

        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        testFilm.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1L);
        testFilm.setGenres(Set.of(genre));
    }

    @Test
    void testAddFilm() {
        Film addedFilm = filmStorage.addFilm(testFilm);

        assertThat(addedFilm.getId()).isNotNull();

        Integer filmCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM films WHERE id = ?",
                Integer.class,
                addedFilm.getId()
        );
        assertThat(filmCount).isEqualTo(1);

        Integer genreCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_genres WHERE film_id = ?",
                Integer.class,
                addedFilm.getId()
        );
        assertThat(genreCount).isEqualTo(1);
    }

    @Test
    void testUpdateFilm() {
        Film addedFilm = filmStorage.addFilm(testFilm);
        addedFilm.setName("Updated Film");

        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");

        String name = jdbcTemplate.queryForObject(
                "SELECT name FROM films WHERE id = ?",
                String.class,
                addedFilm.getId()
        );
        assertThat(name).isEqualTo("Updated Film");
    }


    @Test
    void testGetAllFilms() {
        filmStorage.addFilm(testFilm);

        Film anotherFilm = new Film();
        anotherFilm.setName("Another Film");
        anotherFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        anotherFilm.setDuration(90);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        anotherFilm.setMpa(mpa);
        filmStorage.addFilm(anotherFilm);

        List<Film> films = filmStorage.getAllFilms();
        assertThat(films).hasSize(2);
    }

    @Test
    void testAddLike() {
        Film film = filmStorage.addFilm(testFilm);
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("userLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        jdbcTemplate.update(
                "INSERT INTO users (email, login, birthday) VALUES (?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getBirthday()
        );
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE email = ?",
                Long.class, user.getEmail());

        filmStorage.addLike(film.getId(), userId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                film.getId(), userId
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testRemoveLike() {
        Film film = filmStorage.addFilm(testFilm);
        User user = new User();
        user.setEmail("user@mail.com");
        user.setLogin("userLogin");
        jdbcTemplate.update(
                "INSERT INTO users (email, login) VALUES (?, ?)",
                user.getEmail(), user.getLogin()
        );
        Long userId = jdbcTemplate.queryForObject("SELECT id FROM users WHERE email = ?",
                Long.class, user.getEmail());

        jdbcTemplate.update(
                "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)",
                film.getId(), userId
        );

        filmStorage.removeLike(film.getId(), userId);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                film.getId(), userId
        );
        assertThat(count).isEqualTo(0);
    }
}
