package ru.skypro.homework.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.repository.AdvertisementRepository;
import java.io.File;
import ru.skypro.homework.service.FileStorageService; // Новый сервис для безопасности

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final AdvertisementRepository adRepository;
    private final FileStorageService fileStorageService;

    public ImageController(AdvertisementRepository adRepository, FileStorageService fileStorageService) {
        this.adRepository = adRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * GET /images/{id} - Получение изображения по ID объявления
     */
    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable Long id) throws Exception {
        // 1. Находим объявление в БД по ID
        AdvertisementEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено")); // Или ваше NotFoundException

        // 2. Проверяем, есть ли картинка
        String imagePath = ad.getImagePath();
        if (imagePath == null || imagePath.isEmpty()) {
            throw new FileNotFoundException("Изображение для этого объявления не найдено");
        }

        // 3. Используем сервис для безопасной загрузки файла
        File imageFile = fileStorageService.loadFileAsResource(imagePath);

        // 4. Определяем Content-Type (браузер поймет, как отобразить картинку)
        MediaType mediaType = MediaType.IMAGE_JPEG; // По умолчанию JPEG
        if (imagePath.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (imagePath.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        }

        // 5. Возвращаем файл в теле ответа
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                .body(new InputStreamResource(new FileInputStream(imageFile)));
    }
}
