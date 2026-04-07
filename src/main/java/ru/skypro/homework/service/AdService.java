package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdvertisementEntity;

import java.util.List;

public interface AdService {
    List<Ad> getAllAds();
    Ad createAd(CreateOrUpdateAd adDto, MultipartFile image, String authorEmail);
    Ad getAdById(Long id);

    /**
     * Удаляет объявление, если у пользователя есть доступ.
     */
    void deleteAd(Long id);

    /**
     * Обновляет поля объявления, если у пользователя есть доступ.
     */
    AdvertisementEntity updateAd(Long id, CreateOrUpdateAd request);
    List<AdvertisementEntity> getMyAds(String email);

    /**
     * Обновляет изображение объявления, если у пользователя есть доступ.
     */
    AdvertisementEntity updateAdImage(Long id, MultipartFile file);
}
