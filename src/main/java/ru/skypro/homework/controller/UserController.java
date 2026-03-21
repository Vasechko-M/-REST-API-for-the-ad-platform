package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.NewPassword;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    /**
     *POST /users/set_password - Обновление пароля
     */
    @PostMapping("/set_password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody NewPassword request) {

        return ResponseEntity.ok().build();
    }

}