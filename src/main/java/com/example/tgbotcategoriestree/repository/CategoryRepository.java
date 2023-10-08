package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.Category;
import com.example.tgbotcategoriestree.models.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository class of category
 *
 * @author enovak89
 */
@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String elementName);

    List<Category> findAllByParentCategoryName(String parentName);

    void deleteByName(String elementName);
}
