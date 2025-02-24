package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

/**
 * Represents a film in the Filmorate application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    /** Unique identifier for the film. */
    private Long id;

    /** Title of the film. */
    @NotBlank(message = "title can`t be empty")
    private String title;

    /** Description of the film. */
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Length invalid")
    private String description;

    /** Release date of the film. */
    private LocalDate releaseDate;

    /** Duration of the film in minutes. */
    @Positive(message = "Film duration can not be negative")
    private int duration;

    /** Maximum allowed length for the film description. */
    private static final int MAX_DESCRIPTION_LENGTH = 200;
}
