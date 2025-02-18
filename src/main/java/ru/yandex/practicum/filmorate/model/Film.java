package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "title can`t be empty")
    private String title;
    @Size(max = 200, message = "Max description length is 200 symbols")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Film duration can not be negative")
    private int duration;

    public void validateReleaseDate(LocalDate releaseDate) {
        LocalDate earliestDate = LocalDate.of(1895, 12, 28);
        if (releaseDate.isBefore(earliestDate)) {
            throw new IllegalArgumentException("Release date cannot be earlier than December 28, 1895");
        }
        this.releaseDate = releaseDate;
    }
}