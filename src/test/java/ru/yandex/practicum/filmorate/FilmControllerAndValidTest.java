package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class FilmControllerAndValidTest {
    private FilmController filmController;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

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
        ResponseEntity<?> response = filmController.updateFilm(film);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> errorResponse = (Map<String, String>) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Фильм с ID 999 не найден.", errorResponse.get("error"));
    }

    @Test
    void testGetAllFilms() {
        filmController.addFilm(new Film(null, "Film1", "Desc1", LocalDate.of(2021, 1, 1), 90));
        filmController.addFilm(new Film(null, "Film2", "Desc2", LocalDate.of(2022, 1, 1), 100));
        ResponseEntity<List<Film>> response = filmController.getAllFilms();
        assertEquals(2, response.getBody().size());
    }


        @Test
        void testFilmWithEmptyTitleShouldFailValidation() {
            // Создаем фильм с пустым заголовком
            Film film = new Film();
            film.setName(""); // Пустой заголовок
            film.setDescription("Valid Description");
            film.setReleaseDate(LocalDate.of(2000, 1, 1));
            film.setDuration(120);
            Set<ConstraintViolation<Film>> violations = validator.validate(film);
            assertFalse(violations.isEmpty());
            ConstraintViolation<Film> violation = violations.iterator().next();
            assertEquals("title can`t be empty", violation.getMessage());
        }


    @Test
    void testAddFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Valid Title");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120); // Отрицательная длительность
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Film duration can not be negative", violation.getMessage());
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
        assertEquals("Invalid release date", exception.getMessage());
    }
}
