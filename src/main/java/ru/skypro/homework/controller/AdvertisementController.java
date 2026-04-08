package ru.skypro.homework.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdService;

import javax.validation.Valid;
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Validated
@Tag(name = "Объявления", description = "Управление объявлениями")
public class AdvertisementController {

    private final AdService adService;

    /**
     * GET /ads - Получение всех объявлений
     */
    @GetMapping
    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = Ads.class)))
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    /**
     * GET /ads/{id} - Получение информации об объявлении
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получение информации об объявлении", operationId = "getAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ExtendedAd.class)))
    public ResponseEntity<ExtendedAd> getAdById(@Parameter(description = "ID объявления") @PathVariable Integer id) {
        return ResponseEntity.ok(adService.getAdById(id));
    }

    /**
     * POST /ads - Создание нового объявления
     */

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавление объявления", operationId = "addAd")
    @ApiResponse(responseCode = "201", description = "Создано",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    public ResponseEntity<Ad> createAd(
            @RequestPart("properties") @Valid CreateOrUpdateAd dto,
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {

        String email = authentication.getName();

        Ad newAd = adService.createAd(dto, image, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(newAd);
    }


    /**
     * DELETE /ads/{id} - Удаление объявления
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление объявления", operationId = "removeAd")
    @ApiResponse(responseCode = "204", description = "Удалено")
    public ResponseEntity<Void> deleteAd(
            @Parameter(description = "ID объявления") @PathVariable Integer id,
            Authentication authentication) {

        String email = authentication.getName();
        adService.deleteAd(id, email);

        log.info("Объявление {} удалено пользователем {}", id, email);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /ads/{id} - Обновление информации об объявлении
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Обновление информации об объявлении", operationId = "updateAds")
    @ApiResponse(responseCode = "200", description = "Обновлено",
            content = @Content(schema = @Schema(implementation = Ad.class)))
    public ResponseEntity<Ad> updateAd(
            @Parameter(description = "ID объявления") @PathVariable Integer id,
            @Parameter(description = "Новые данные объявления") @Valid @RequestBody CreateOrUpdateAd request,
            Authentication authentication) {

        String email = authentication.getName();
        Ad updatedAd = adService.updateAd(id, request, email);

        log.info("Объявление {} обновлено пользователем {}", id, email);
        return ResponseEntity.ok(updatedAd);
    }

    /**
     * GET /ads/me - Получение объявлений текущего пользователя
     */
    @GetMapping("/me")
    @Operation(summary = "Получение объявлений авторизованного пользователя", operationId = "getAdsMe")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = Ads.class)))
    public ResponseEntity<Ads> getMyAds(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(adService.getMyAds(email));
    }

    /**
     * PATCH /ads/{id}/image - Обновление изображения объявления
     */
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление изображения объявления", operationId = "updateImage")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
    public ResponseEntity<byte[]> updateImage(
            @Parameter(description = "ID объявления") @PathVariable Integer id,
            @Parameter(description = "Файл изображения") @RequestParam("image") MultipartFile file,
            Authentication authentication) {

        String email = authentication.getName();
        byte[] imageBytes = adService.updateAdImage(id, file, email);

        log.info("Изображение объявления {} обновлено пользователем {}", id, email);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(imageBytes);
    }
}