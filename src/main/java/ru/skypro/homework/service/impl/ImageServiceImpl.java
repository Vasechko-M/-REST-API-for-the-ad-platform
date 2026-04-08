package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${images.upload.dir:./uploads/images}")
    private String uploadDir;

    // Допустимые типы файлов
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/bmp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Override
    public String saveImage(MultipartFile file) {
        // 1. Проверка на пустой файл
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл пустой");
        }

        // 2. Проверка типа файла (Content-Type)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Неподдерживаемый тип файла. Разрешены: JPEG, PNG, GIF, WEBP, BMP");
        }

        // 3. Проверка расширения файла
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Имя файла отсутствует");
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Неподдерживаемое расширение файла. Разрешены: .jpg, .jpeg, .png, .gif, .webp, .bmp");
        }

        try {
            // 4. Создаем директорию если не существует
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Создана директория для загрузки изображений: {}", uploadPath.toAbsolutePath());
            }

            // 5. Генерируем уникальное имя файла (UUID)
            String filename = UUID.randomUUID().toString() + extension;

            // 6. Сохраняем файл
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            log.info("Сохранено изображение: {} (размер: {} байт)", filePath, file.getSize());

            // 7. Возвращаем относительный путь для доступа
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при сохранении изображения", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Из URL получаем имя файла
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path imagePath = Paths.get(uploadDir, filename);

            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                log.info("Удалено изображение: {}", imagePath);
            } else {
                log.warn("Изображение не найдено для удаления: {}", imagePath);
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения: {}", e.getMessage());
        }
    }

    @Override
    public byte[] getImage(String filename) {
        try {
            Path imagePath = Paths.get(uploadDir, filename);
            if (!Files.exists(imagePath)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Изображение не найдено");
            }
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            log.error("Ошибка при чтении изображения: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при чтении изображения", e);
        }
    }
}