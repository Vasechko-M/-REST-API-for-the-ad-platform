package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.Ad;
import ru.skypro.homework.dto.CreateOrUpdateAd;
import ru.skypro.homework.entity.AdvertisementEntity;
import ru.skypro.homework.entity.UserEntity;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdvertisementRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.util.List;

@Slf4j
@Service
public class AdServiceImpl implements AdService {

    private final AdvertisementRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public AdServiceImpl(AdvertisementRepository adRepository,
                         AdMapper adMapper,
                         UserRepository userRepository,
                         ImageService imageService) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    @Override
    public List<Ad> getAllAds() {
        List<AdvertisementEntity> ads = adRepository.findAll();
        return ads.stream()
                .map(adMapper::toDto)
                .toList();
    }

    @Override
    public Ad createAd(CreateOrUpdateAd adDto, MultipartFile image, String authorEmail) {
        AdvertisementEntity entity = adMapper.toEntity(adDto);

        if (image != null && !image.isEmpty()) {
            String imageUrl = imageService.saveImage(image);
            entity.setImage(imageUrl);
        }

        UserEntity author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        entity.setAuthor(author);

        AdvertisementEntity saved = adRepository.save(entity);
        log.info("Создано объявление: {}", saved.getTitle());

        return adMapper.toDto(saved);
    }

    @Override
    public Ad getAdById(Long id) {
        AdvertisementEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        return adMapper.toDto(ad);
    }
    @PreAuthorize("@adSecurity.hasAccess(#id)")
    @Override
    public void deleteAd(Long id) {
        AdvertisementEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

        // Удаляем картинку если она есть
        if (ad.getImage() != null && !ad.getImage().isEmpty()) {
            imageService.deleteImage(ad.getImage());
        }

        adRepository.delete(ad);
        log.info("Удалено объявление с id: {}", id);
    }

    @PreAuthorize("@adSecurity.hasAccess(#id)")
    @Override
    public AdvertisementEntity updateAd(Long id, CreateOrUpdateAd request) {
        AdvertisementEntity existingAd = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

        if (request.getTitle() != null) existingAd.setTitle(request.getTitle());
        if (request.getPrice() != null) existingAd.setPrice(request.getPrice());
        if (request.getDescription() != null) existingAd.setDescription(request.getDescription());

        AdvertisementEntity saved = adRepository.save(existingAd);
        log.info("Обновлено объявление с id: {}", id);

        return saved;
    }

    @Override
    public List<AdvertisementEntity> getMyAds(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        return adRepository.findAllByAuthor(user);
    }
    @PreAuthorize("@adSecurity.hasAccess(#id)")
    @Override
    public AdvertisementEntity updateAdImage(Long id, MultipartFile file) {
        AdvertisementEntity ad = adRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Файл пустой");
        }

        // Удаляем старую картинку если была
        if (ad.getImage() != null && !ad.getImage().isEmpty()) {
            imageService.deleteImage(ad.getImage());
        }

        // Сохраняем новую картинку
        String imageUrl = imageService.saveImage(file);
        ad.setImage(imageUrl);

        AdvertisementEntity saved = adRepository.save(ad);
        log.info("Обновлена картинка для объявления id: {}", id);

        return saved;
    }
    public boolean isAuthor(Long adId, String username) {
        AdvertisementEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Объявление не найдено"));
        return ad.getAuthor().getEmail().equals(username);
    }
}