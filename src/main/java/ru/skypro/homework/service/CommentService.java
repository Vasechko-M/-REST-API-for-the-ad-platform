package ru.skypro.homework.service;

import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.CreateOrUpdateComment;

import java.util.List;

public interface CommentService {
    /**
     * Возвращает комментарии по идентификатору объявления.
     */
    List<Comment> getCommentsByAdId(Integer adId);
    /**
     * Добавляет комментарий к объявлению от текущего пользователя.
     */
    Comment addComment(Integer adId, CreateOrUpdateComment commentRequest, String authorEmail);
    /**
     * Обновляет комментарий, если у пользователя есть доступ.
     */
    Comment updateComment(Integer adId, Integer commentId, CreateOrUpdateComment commentRequest);
    /**
     * Удаляет комментарий, если у пользователя есть доступ.
     */
    void deleteComment(Integer adId, Integer commentId);
}