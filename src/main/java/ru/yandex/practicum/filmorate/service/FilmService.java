package ru.yandex.practicum.filmorate.service;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.xml.bind.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;



@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) throws ValidationException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws NotFoundException {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Optional<Film> getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(Long filmId, Long userId) throws NotFoundException {
        Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new RuntimeException("Film not found"));
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>()); // Инициализируем, если null
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        return film;
    }


    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() -> new RuntimeException("Film not found"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getAllFilms();
        films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        return films.subList(0, Math.min(count, films.size()));
    }
}