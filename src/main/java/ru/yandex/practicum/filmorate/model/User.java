package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**Unique user ID. */
    private Long id;

    /**Users email address. */
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введенный email не соответствует формату email-адресов!")
    private String email;

    /**Users login. */
    @NotBlank(message = "Логин не может быть пустым!")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы!")
    private String login;

    /**Users name. */
    private String name;

    /**Users birthday. */
    @Past(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}
