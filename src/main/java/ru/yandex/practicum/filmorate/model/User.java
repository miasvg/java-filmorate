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
    private Long id;
    @Email(message = "Введенный email не соответствует формату email-адресов!")
    private String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы!")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем!")
    private LocalDate birthday;
}