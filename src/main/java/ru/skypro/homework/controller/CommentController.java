package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ads/{adId}/comments")
@Validated
public class CommentController {

    /**
    GET /ads/{adId}/comments — Получение комментариев объявления
     */
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long adId) {
        return ResponseEntity.ok(List.of());
    }

    /**
    POST /ads/{adId}/comments — Добавление комментария к объявлению
     */
    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long adId,
                                              @Valid @RequestBody CreateOrUpdateComment commentRequest) {
        return ResponseEntity.ok(new Comment());
    }

    /**
    PATCH /ads/{adId}/comments/{commentId} — Обновление комментария
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long adId,
                                                 @PathVariable Integer commentId,
                                                 @Valid @RequestBody CreateOrUpdateComment commentRequest) {
        return ResponseEntity.ok(new Comment());
    }

    /**
    DELETE /ads/{adId}/comments/{commentId} — Удаление комментария
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long adId,
                                              @PathVariable Integer commentId) {
        return ResponseEntity.noContent().build();
    }
}