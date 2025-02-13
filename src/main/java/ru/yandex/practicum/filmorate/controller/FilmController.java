package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private long filmIdCounter = 1;

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        film.setId(filmIdCounter++);
        films.put(film.getId(), film);
        log.info("film added: {}", film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Update incorrect: {}", film);
            return ResponseEntity.notFound().build();
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }
}