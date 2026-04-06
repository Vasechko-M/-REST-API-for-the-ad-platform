package ru.skypro.homework.service.impl;
import ru.skypro.homework.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPassword;
import ru.skypro.homework.dto.UpdateUser;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.RoleEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private static final String UPLOAD_DIR = "images/";

    @Override
    public void changePassword(NewPassword newPassword, String email) {

        // 1. Найти пользователя
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Проверка текущего пароля
        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный текущий пароль");
        }

        // 3. Установка нового пароля (с хешированием)
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));

        // 4. Сохранение
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        return userMapper.toDto(user);
    }
    @Override
    public UpdateUser updateUser(UpdateUser updateUser, String email) {

        // 1. Найти пользователя
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Обновляем только переданные поля (PATCH!)
        if (updateUser.getFirstName() != null) {
            user.setFirstName(updateUser.getFirstName());
        }

        if (updateUser.getLastName() != null) {
            user.setLastName(updateUser.getLastName());
        }

        if (updateUser.getPhone() != null) {
            user.setPhone(updateUser.getPhone());
        }

        // 3. Сохраняем
        userRepository.save(user);

        // 4. Возвращаем DTO
        return UpdateUser.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public void updateUserImage(MultipartFile file, String email) {
// 1. Найти пользователя
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Проверка файла
        if (file.isEmpty()) {
            throw new RuntimeException("Файл пустой");
        }

        // 3. Генерация уникального имени
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 4. Сохранение файла
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destination = new File(UPLOAD_DIR + fileName);

        try {
            file.transferTo(destination);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла");
        }

        // 5. Сохраняем путь в БД (Entity)
        user.setImage("/images/" + fileName);

        userRepository.save(user);
    }
    /**
     * Метод для регистрации нового пользователя с ролью USER
     */
    public void registerUser(User userDto, String password) {
        UserEntity userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        // Получаем роль "USER"
        RoleEntity role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));
        userEntity.setRole(role);
        userRepository.save(userEntity);
    }
}
