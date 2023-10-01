package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildCategoryRepository extends JpaRepository<ChildCategory, Long> {
    Optional<ChildCategory> findByName(String element);

    List<ChildCategory> findAllByRootId(Long id);
}
