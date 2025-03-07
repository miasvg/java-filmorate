package ru.yandex.practicum.filmorate.storage;

import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long filmIdCounter = 1;

    @Override
    public Film addFilm(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(filmIdCounter++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws NotFoundException {
 Long id = film.getId();
 if (!films.containsKey(id)) {
     throw new NotFoundException("Пользователь с ID " + id + " не найден");
 }
 films.replace(id, film);
 return film;
    }


    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
    }

    private void validateReleaseDate(final LocalDate releaseDate) {
        final LocalDate localDateMin = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(localDateMin)) {
            throw new IllegalArgumentException();
        }
    }
}
