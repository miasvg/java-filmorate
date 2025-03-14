package ru.yandex.practicum.filmorate.storage;

import javassist.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws NotFoundException;

    List<Film> getAllFilms();

    Optional<Film> getFilmById(Long id);

    void deleteFilm(Long id);
}
