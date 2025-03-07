package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    private FilmService filmService;
    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage);
    }

    @Test
    void testAddLike() throws ValidationException {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film = filmService.addFilm(film);

        filmService.addLike(film.getId(), 1L);
        assertTrue(film.getLikes().contains(1L));
    }

    @Test
    void testRemoveLike() throws ValidationException {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film = filmService.addFilm(film);

        filmService.addLike(film.getId(), 1L);
        filmService.removeLike(film.getId(), 1L);

        assertFalse(film.getLikes().contains(1L));
    }
}

