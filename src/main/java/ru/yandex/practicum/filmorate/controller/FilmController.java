package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing films in the Filmorate application.
 */
@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    /** A map to store films with their IDs. */
    private final Map<Long, Film> films = new HashMap<>();

    /** Counter to generate unique film IDs. */
    private long filmIdCounter = 1;
/** Constant for release date validation. */
    private final LocalDate localDateMin = LocalDate.of(1895, 12, 28);

    /**
     * Adds a new film to the collection.
     *
     * @param film the film to add
     * @return the added film with a generated ID
     */
    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody  Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        validateReleaseDate(releaseDate);
        film.setId(filmIdCounter++);
        films.put(film.getId(), film);
        log.info("film added: {}", film);
        return ResponseEntity.ok(film);
    }

    /**
     * Updates an existing film in the collection.
     *
     * @param film the film to update
     * @return the updated film if found, otherwise a 404 response
     */
    @PutMapping
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Попытка обновить несуществующий фильм: {}", film);
            // Возвращаем JSON с сообщением об ошибке
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Фильм с ID " + film.getId() + " не найден.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен: {}", film);
        return ResponseEntity.ok(film);
    }

    /**
     * Retrieves all films in the collection.
     *
     * @return a list of all films
     */
    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    /**
     * Validates the release date of a film.
     *
     * @param releaseDate the release date to validate
     * @throws IllegalArgumentException if the release date is invalid
     */
    private void validateReleaseDate(final LocalDate releaseDate) {
        if (releaseDate.isBefore(localDateMin)) {
            throw new IllegalArgumentException("Invalid release date");
        }
    }
}
