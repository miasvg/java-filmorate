package ru.yandex.practicum.filmorate.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings();
    }

    public Mpa getMpaRatingById(Long id) throws NotFoundException {
        return mpaStorage.getMpaRatingById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id=" + id + " не найден"));
    }
}
