package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new Mpa(rs.getLong("mpa_id"), rs.getString("mpa_name")));
        film.setGenres(getGenresByFilmId(rs.getLong("id")));
        return film;
    };

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        // Сохраняем основные данные фильма
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        // Сохраняем жанры
        saveFilmGenres(film);

        return film;
    }

    private void saveFilmGenres(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            List<Object[]> batchArgs = uniqueGenres.stream()
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .collect(Collectors.toList());

            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        // Получаем все фильмы с MPA
        String filmsSql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_id = m.id";

        List<Film> films = jdbcTemplate.query(filmsSql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);

            return film;
        });

        if (!films.isEmpty()) {
            Map<Long, Set<Genre>> filmGenresMap = loadAllFilmGenres();
            films.forEach(film ->
                    film.setGenres(filmGenresMap.getOrDefault(film.getId(), Collections.emptySet()))
            );
        }
        return films;
    }


    @Override
    public Optional<Film> getFilmById(Long id) {
        // Основные данные фильма
        String filmSql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

        try {
            Film film = jdbcTemplate.queryForObject(filmSql, (rs, rowNum) -> {
                Film f = new Film();
                f.setId(rs.getLong("id"));
                f.setName(rs.getString("name"));
                f.setDescription(rs.getString("description"));
                f.setReleaseDate(rs.getDate("release_date").toLocalDate());
                f.setDuration(rs.getInt("duration"));
                Mpa mpa = new Mpa();
                mpa.setId(rs.getLong("mpa_id"));
                mpa.setName(rs.getString("mpa_name"));
                f.setMpa(mpa);

                return f;
            }, id);
            // Загрузка жанров в Set
            String genresSql = "SELECT g.id, g.name FROM genres g " +
                    "JOIN film_genres fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = ?";

            Set<Genre> genres = new HashSet<>(
                    jdbcTemplate.query(genresSql, (rs, rowNum) -> {
                        Genre genre = new Genre();
                        genre.setId(rs.getLong("id"));
                        genre.setName(rs.getString("name"));
                        return genre;
                    }, id)
            );
            film.setGenres(genres);
            return Optional.of(film);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int deleted = jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")), filmId);
        return new HashSet<>(genres);
    }

    private Map<Long, Set<Genre>> loadAllFilmGenres() {
        String sql = "SELECT fg.film_id, g.id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id";

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Set<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return result;
        });
    }

    public List<Film> getPopularFilms(int count) {
        String sql = """
        SELECT f.*, m.name AS mpa_name, COUNT(fl.user_id) AS likes_count
        FROM films f
        JOIN mpa_ratings m ON f.mpa_id = m.id
        LEFT JOIN film_likes fl ON f.id = fl.film_id
        GROUP BY f.id, m.name
        ORDER BY likes_count DESC
        LIMIT ?
        """;

        List<Film> popularFilms = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            film.setMpa(mpa);
            return film;
        }, count);

        // Загружаем жанры для всех фильмов одним запросом
        if (!popularFilms.isEmpty()) {
            Map<Long, Set<Genre>> filmGenresMap = loadFilmGenresForFilms(
                    popularFilms.stream().map(Film::getId).collect(Collectors.toList())
            );

            popularFilms.forEach(film ->
                    film.setGenres(filmGenresMap.getOrDefault(film.getId(), Collections.emptySet()))
            );
        }

        return popularFilms;
    }

    private Map<Long, Set<Genre>> loadFilmGenresForFilms(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
}

String sql = """
SELECT fg.film_id, g.id, g.name
FROM film_genres fg
JOIN genres g ON fg.genre_id = g.id
WHERE fg.film_id IN (%s)
""".formatted(filmIds.stream().map(String::valueOf).collect(Collectors.joining(",")));

        return jdbcTemplate.query(sql, rs -> {
            Map<Long, Set<Genre>> result = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");

                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));

                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return result;
        });
    }
}

