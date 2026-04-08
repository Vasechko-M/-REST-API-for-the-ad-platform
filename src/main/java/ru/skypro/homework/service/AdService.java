package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.Ads;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.dto.ExtendedAd;

public interface AdService {

    Ads getAllAds();

    /**
     * Создает объявление для пользователя с указанным email
     */
    Ad createAd(CreateOrUpdateAd adDto, MultipartFile image, String email);

    ExtendedAd getAdById(Integer id);

    /**
     * Удаляет объявление, если у пользователя с указанным email есть доступ
     */
    void deleteAd(Integer id, String email);

    /**
     * Обновляет объявление для пользователя с указанным email
     */
    Ad updateAd(Integer id, CreateOrUpdateAd request, String email);

    /**
     * Получить объявления пользователя по email
     */
    Ads getMyAds(String email);

    /**
     * Обновляет изображение объявления для пользователя с указанным email
     */
    byte[] updateAdImage(Integer id, MultipartFile file, String email);
}