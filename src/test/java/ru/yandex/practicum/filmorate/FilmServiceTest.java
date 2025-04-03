package ru.yandex.practicum.filmorate;

import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;
    private InMemoryUserStorage userStorage;
    private JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;
    private GenreStorage genreStorage;
    private FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate();
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
        userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmDbStorage, filmStorage, userStorage, genreStorage);
    }

    @Test
    void testAddLike() throws ValidationException, NotFoundException {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film = filmService.addFilm(film);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(film.getId(), 1L);
        assertTrue(film.getLikes().contains(1L));
    }

    @Test
    void testRemoveLike() throws ValidationException, NotFoundException {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film = filmService.addFilm(film);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user);

        filmService.addLike(film.getId(), 1L);
        filmService.removeLike(film.getId(), 1L);

        assertFalse(film.getLikes().contains(1L));
    }
}

