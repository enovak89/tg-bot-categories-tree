package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Repository class of category
 *
 * @author enovak89
 */
@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String elementName);

    Set<Category> findAllByParentCategoryName(String parentName);

    Set<Category> findAllByParentCategoryNameNull();

    void deleteByName(String elementName);
}
