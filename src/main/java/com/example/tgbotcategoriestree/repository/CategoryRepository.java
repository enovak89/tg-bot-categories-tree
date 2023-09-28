package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.RootCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<RootCategory, Long> {
}
