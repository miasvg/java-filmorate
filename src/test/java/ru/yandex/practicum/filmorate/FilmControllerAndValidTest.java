package ru.yandex.practicum.filmorate;

import javassist.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class FilmControllerAndValidTest {
    private FilmController filmController;
    private InMemoryFilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void testAddFilm() throws ValidationException {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        ResponseEntity<Film> response = filmController.addFilm(film);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void testUpdateFilmNotFound() throws NotFoundException {
        Film film = new Film();
        film.setId(999L);
        film.setName("Updated Film");
        film.setDescription("Updated Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> filmController.updateFilm(film)
        );
        assertEquals("Фильм с ID 999 не найден", exception.getMessage());
    }

    @Test
    void testGetAllFilms() throws ValidationException {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2021, 1, 1));
        film1.setDuration(90);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2022, 1, 1));
        film2.setDuration(100);

        filmController.addFilm(film1);
        filmController.addFilm(film2);

        ResponseEntity<List<Film>> response = filmController.getAllFilms();
        assertEquals(2, response.getBody().size());
    }
}

