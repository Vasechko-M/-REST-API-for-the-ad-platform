package ru.skypro.homework.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Ad {

    private Integer pk;
    private Integer author;
    private String image;
    private Integer price;
    private String title;

    public Ad() {
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Для фронта: формирует URL изображения на основе pk объявления
     */
    @JsonProperty("image")
    public String getImageUrl() {
        if (this.pk == null) {
            return null;
        }
        return "/images/" + this.pk;
    }

//    /**
//     * Статический метод для быстрого создания DTO из Entity
//     */
//    public static Ad fromEntity(ru.skypro.homework.entity.AdEntity entity) {
//        Ad ad = new Ad();
//        ad.setPk(entity.getId());
//        ad.setAuthor(entity.getAuthor().getId());
//        ad.setPrice(entity.getPrice());
//        ad.setTitle(entity.getTitle());
//        ad.setImage(entity.getImagePath());
//        return ad;
//    }
}