package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.RootCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RootCategoryRepository extends JpaRepository<RootCategory, Long> {
    Optional<RootCategory> findByName(String element);
}
