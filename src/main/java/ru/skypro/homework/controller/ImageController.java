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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ImageUploadResponse;
import ru.skypro.homework.service.ImageService;

@Slf4j
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
@Tag(name = "Изображения", description = "Загрузка и получение изображений")
public class ImageController {

    private final ImageService imageService;

    /**
     * POST /images/upload - Загрузка изображения на сервер
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка изображения", operationId = "uploadImage")
    @ApiResponse(responseCode = "201", description = "Изображение успешно загружено",
            content = @Content(schema = @Schema(implementation = ImageUploadResponse.class)))
    @ApiResponse(responseCode = "400", description = "Неподдерживаемый тип файла или файл пустой")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Ошибка при сохранении файла")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @Parameter(description = "Файл изображения", required = true)
            @RequestParam("image") MultipartFile file) {

        log.info("Запрос на загрузку изображения: {}", file.getOriginalFilename());

        // Сохраняем изображение и получаем URL
        String imageUrl = imageService.saveImage(file);

        // Извлекаем UUID из URL
        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        String uuid = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;

        // Формируем ответ
        ImageUploadResponse response = ImageUploadResponse.builder()
                .id(uuid)
                .url(imageUrl)
                .size(file.getSize())
                .contentType(file.getContentType())
                .originalFilename(file.getOriginalFilename())
                .build();

        log.info("Изображение загружено: {} -> {}", file.getOriginalFilename(), imageUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /images/{id} - Получение изображения по ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение изображения по ID", operationId = "getImage")
    @ApiResponse(responseCode = "200", description = "Изображение найдено")
    @ApiResponse(responseCode = "404", description = "Изображение не найдено")
    public ResponseEntity<byte[]> getImage(
            @Parameter(description = "ID изображения (UUID с расширением или без)", required = true)
            @PathVariable String id) {

        log.info("Запрос на получение изображения: {}", id);

        // Если ID уже содержит расширение, используем как есть
        String filename = id;

        // Если расширения нет, пробуем найти файл с разными расширениями
        if (!id.contains(".")) {
            String[] extensions = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"};
            byte[] imageBytes = null;
            String foundExtension = "";

            for (String ext : extensions) {
                try {
                    imageBytes = imageService.getImage(id + ext);
                    foundExtension = ext;
                    filename = id + ext;
                    break;
                } catch (Exception e) {
                    // Продолжаем поиск
                }
            }

            if (imageBytes == null) {
                log.warn("Изображение не найдено: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Определяем Content-Type по расширению
            String contentType = switch (foundExtension) {
                case ".png" -> MediaType.IMAGE_PNG_VALUE;
                case ".gif" -> MediaType.IMAGE_GIF_VALUE;
                case ".webp" -> "image/webp";
                case ".bmp" -> "image/bmp";
                default -> MediaType.IMAGE_JPEG_VALUE;
            };

            byte[] finalImageBytes = imageBytes;
            log.info("Изображение отправлено: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(finalImageBytes);
        }

        // Если расширение есть, ищем напрямую
        try {
            byte[] imageBytes = imageService.getImage(filename);

            // Определяем Content-Type по расширению
            String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
            String contentType = switch (extension) {
                case ".png" -> MediaType.IMAGE_PNG_VALUE;
                case ".gif" -> MediaType.IMAGE_GIF_VALUE;
                case ".webp" -> "image/webp";
                case ".bmp" -> "image/bmp";
                default -> MediaType.IMAGE_JPEG_VALUE;
            };

            log.info("Изображение отправлено: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            log.warn("Изображение не найдено: {}", filename);
            return ResponseEntity.notFound().build();
        }
    }
}
