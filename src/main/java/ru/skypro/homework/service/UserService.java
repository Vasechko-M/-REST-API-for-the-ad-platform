package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;

public interface UserService {
    /**
     * Смена пароля текущего пользователя.
     */
    void changePassword(NewPassword newPassword, String email);

    /**
     * Возвращает профиль текущего пользователя.
     */
    User getCurrentUser(String email);

    /**
     * Обновляет профиль текущего пользователя.
     */
    UpdateUser updateUser(UpdateUser updateUser, String email);

    /**
     * Обновляет аватар текущего пользователя.
     */
    void updateUserImage(MultipartFile file, String email);

    /**
     * Регистрирует нового пользователя с паролем.
     */
    void registerUser(User user, String password);
}
