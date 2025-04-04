package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Genre(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Set<Long> getExistingGenreIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptySet();
        }
        String sql = "SELECT id FROM genres WHERE id IN (" +
                ids.stream().map(String::valueOf).collect(Collectors.joining(",")) +
                ")";

        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class));
    }
}
