package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface ChildCategoryRepository extends JpaRepository<ChildCategory, Long> {
    Optional<ChildCategory> findByName(String elementName);

    void deleteByName(String elementName);
}
