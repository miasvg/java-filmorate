package ru.yandex.practicum.filmorate.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public ResponseEntity<List<Mpa>> getAllMpaRatings() {
        return ResponseEntity.ok(mpaService.getAllMpaRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mpa> getMpaRatingById(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(mpaService.getMpaRatingById(id));
    }
}

