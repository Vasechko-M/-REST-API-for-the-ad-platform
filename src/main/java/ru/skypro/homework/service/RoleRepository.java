package ru.skypro.homework.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skypro.homework.entity.RoleEntity;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(String name);
}
