package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerAndValidTest {
    private FilmController filmController;
    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }
    @Test
    void testAddFilm() {
        Film film = new Film(null, "Test Film", "Description", LocalDate.of(2020, 1, 1), 120);
        ResponseEntity<Film> response = filmController.addFilm(film);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void testUpdateFilmNotFound() {
        Film film = new Film(999L, "Updated Film", "Updated Description", LocalDate.of(2020, 1, 1), 120);
        ResponseEntity<Film> response = filmController.updateFilm(film);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAllFilms() {
        filmController.addFilm(new Film(null, "Film1", "Desc1", LocalDate.of(2021, 1, 1), 90));
        filmController.addFilm(new Film(null, "Film2", "Desc2", LocalDate.of(2022, 1, 1), 100));
        ResponseEntity<List<Film>> response = filmController.getAllFilms();
        assertEquals(2, response.getBody().size());
    }
    @Test
    void testAddFilmWithEmptyTitle() {
        Film film = new Film(null, "", "Description", LocalDate.of(2020, 1, 1), 120);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> filmController.addFilm(film));
        assertEquals("title can't be empty", exception.getMessage());
    }

    @Test
    void testAddFilmWithMaxDescriptionLength() {
        String longDescription = "A".repeat(200);
        Film film = new Film(null, "Valid Title", longDescription, LocalDate.of(2020, 1, 1), 120);
        ResponseEntity<Film> response = filmController.addFilm(film);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testAddFilmWithInvalidReleaseDate() {
        Film film = new Film(null, "Invalid Film", "Description", LocalDate.of(1895, 12, 27), 120);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> filmController.addFilm(film));
        assertEquals("Release date cannot be earlier than December 28, 1895", exception.getMessage());
    }

    @Test
    void testAddFilmWithNegativeDuration() {
        Film film = new Film(null, "Valid Film", "Description", LocalDate.of(2020, 1, 1), -100);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> filmController.addFilm(film));
        assertEquals("Film duration cannot be negative", exception.getMessage());
    }
}
