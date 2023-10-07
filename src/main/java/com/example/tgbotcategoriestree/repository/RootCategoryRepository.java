package com.example.tgbotcategoriestree.repository;

import com.example.tgbotcategoriestree.models.RootCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository class of root category
 *
 * @author enovak89
 */
@Repository
@Transactional
public interface RootCategoryRepository extends JpaRepository<RootCategory, Long> {
    Optional<RootCategory> findByName(String elementName);

    void deleteByName(String elementName);
}
