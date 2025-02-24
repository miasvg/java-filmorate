package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
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
    @Email(message = "Введенный email не соответствует формату email-адресов!")
    private String email;

    /**Users login. */
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы!")
    private String login;

    /**Users name. */
    private String name;

    /**Users birthday. */
    @Past(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}
