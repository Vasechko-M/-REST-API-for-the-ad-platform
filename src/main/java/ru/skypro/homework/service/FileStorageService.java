package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/images/}")
    private String uploadDir;

    public File loadFileAsResource(String filePath) throws FileNotFoundException {
        try {
            // Получаем абсолютный путь к папке загрузок
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // Собираем полный путь к файлу
            Path file = uploadPath.resolve(filePath).normalize();

            // БЕЗОПАСНОСТЬ: Проверяем, что файл находится внутри папки uploads
            if (!file.startsWith(uploadPath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл не найден");
            }

            File imageFile = file.toFile();
            if (imageFile.exists()) {
                return imageFile;
            } else {
                throw new FileNotFoundException("Файл не найден: " + filePath);
            }
        } catch (Exception e) {
            throw new FileNotFoundException("Ошибка доступа к файлу");
        }
    }
}