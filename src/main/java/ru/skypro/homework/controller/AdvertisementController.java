package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ads")
@Validated
public class AdvertisementController {

    /**
    GET /ads — Получение всех объявлений
     */
    @GetMapping
    public ResponseEntity<List<Ad>> getAllAds() {

        return ResponseEntity.ok(List.of());
    }

    /**
    POST /ads — Создание объявления
    */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Ad> createAd(
            @RequestPart("properties") @Valid CreateOrUpdateAd properties,
            @RequestPart("image") MultipartFile image) {

        return ResponseEntity.status(201).body(new Ad());
    }

    /**
    GET /ads/{id} — Получение информации об объявлении
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ad> getAdById(@PathVariable Integer id) {

        return ResponseEntity.ok(new Ad());
    }

    /**
    DELETE /ads/{id} — Удаление объявления
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable Integer id) {

        return ResponseEntity.ok().build();
    }

    /**
    PATCH /ads/{id} — Обновление информации об объявлении
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable Integer id, @Valid @RequestBody CreateOrUpdateAd request) {

        return ResponseEntity.ok(new Ad());
    }

    /**
    GET /ads/me — Получение объявлений авторизованного пользователя
     */
    @GetMapping("/me")
    public ResponseEntity<List<Ad>> getMyAds() {

        return ResponseEntity.ok(List.of());
    }
    /**
     * PATCH /ads/{id}/image - Обновление картинки объявления
     */
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление картинки объявления",
            operationId = "updateImage",
            tags = {"Объявления"}
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "array", format = "binary")
            )
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<byte[]> updateImage(
            @Parameter(description = "ID объявления", required = true, example = "123")
            @PathVariable Integer id,

            @RequestParam("image") MultipartFile file) {

        // TODO: Реализовать логику сохранения изображения

        // Пустой ответ-плейсхолдер
        return ResponseEntity.ok().build();
    }
}