package ru.yandex.practicum.filmorate.service;


import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import javax.xml.bind.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class FilmService {
    private final FilmDbStorage filmDbStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmDbStorage filmDbStorage, @Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage, GenreStorage genreStorage) {
        this.filmDbStorage = filmDbStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    public Film addFilm(Film film) throws NotFoundException, ValidationException {
        validateReleaseDate(film.getReleaseDate());
        validateFilmGenres(film.getGenres());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws NotFoundException {
        if (film.getId() == null || !filmStorage.getFilmById(film.getId()).isPresent()) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Optional<Film> getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(Long filmId, Long userId) throws NotFoundException {
        // Проверяем существование фильма и пользователя
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        filmDbStorage.addLike(filmId, userId);
    }


    public void removeLike(Long filmId, Long userId) throws NotFoundException {
        // Проверяем существование фильма и пользователя
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        filmDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmDbStorage.getPopularFilms(count);
    }

    private void validateFilmGenres(Set<Genre> genres) throws NotFoundException {
        if (genres != null && !genres.isEmpty()) {
            Set<Long> genreIds = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            // Один запрос для проверки всех жанров
            Set<Long> existingIds = genreStorage.getExistingGenreIds(genreIds);

            Set<Long> notFoundIds = genreIds.stream()
                    .filter(id -> !existingIds.contains(id))
                    .collect(Collectors.toSet());
            if (!notFoundIds.isEmpty()) {
                throw new NotFoundException("Не найдены жанры с id: " + notFoundIds);
            }
        }
    }

    private void validateReleaseDate(final LocalDate releaseDate) {
        final LocalDate localDateMin = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(localDateMin)) {
            throw new IllegalArgumentException();
        }
    }
}