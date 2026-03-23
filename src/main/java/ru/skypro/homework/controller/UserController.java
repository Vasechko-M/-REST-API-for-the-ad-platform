package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "Пользователи", description = "Управление профилем пользователя")
public class UserController {

    /**
     * POST /users/set_password - Обновление пароля
     */
    @PostMapping("/set_password")
    @Operation(summary = "Обновление пароля", operationId = "setPassword")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody NewPassword request) {
        // TODO: Реализация логики смены пароля
        return ResponseEntity.ok().build();
    }

    /**
     * GET /users/me - Получение информации об авторизованном пользователе
     */
    @GetMapping("/me")
    @Operation(summary = "Получение информации об авторизованном пользователе", operationId = "getUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<User> getCurrentUser() {
        // TODO: Реализация получения текущего пользователя
        User user = User.builder()
                .id(1)
                .email("user@mail.ru")
                .firstName("Иван")
                .lastName("Иванов")
                .build();
        return ResponseEntity.ok(user);
    }

    /**
     * PATCH /users/me - Обновление информации об авторизованном пользователе
     */
    @PatchMapping("/me")
    @Operation(summary = "Обновление информации об авторизованном пользователе", operationId = "updateUser")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = UpdateUser.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<UpdateUser> updateUserInfo(@Valid @RequestBody UpdateUser updateData) {
        // TODO: Реализация обновления данных

        // Возвращаем обновленный DTO, как указано в требованиях для PUT/PATCH
        return ResponseEntity.ok(updateData);
    }

    /**
     * PATCH /users/me/image - Обновление аватара авторизованного пользователя
     */
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновление аватара авторизованного пользователя", operationId = "updateUserImage")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Файл изображения",
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = UpdateUserImageRequest.class)
            )
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Void> updateUserImage(
            @Parameter(description = "Файл изображения") @RequestParam("image") MultipartFile file) {

        // TODO: Реализация загрузки файла (MultipartFile)

        return ResponseEntity.ok().build();
    }
}