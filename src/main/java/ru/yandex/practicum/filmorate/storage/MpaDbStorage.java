package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Optional<Mpa> getMpaRatingById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Mpa(rs.getLong("id"), rs.getString("name")), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
