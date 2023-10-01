package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildCategoryRepository extends JpaRepository<RootCategory, Long> {
    Optional<ChildCategory> findByName(String element);
}
