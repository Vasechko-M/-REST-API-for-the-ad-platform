package ru.skypro.homework.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO-класс, представляющий данные для входа в систему.
 * Включает имя пользователя (логин) и пароль.
 */
@Data
public class Login {

    @NotBlank
    @Size(min = 2, max = 16)
    private String username;

    @NotBlank
    @Size(min = 2, max = 16)
    private String password;
}
