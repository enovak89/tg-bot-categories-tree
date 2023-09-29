package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.CategoryRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void addRootElement(String element) {
        RootCategory rootCategory = new RootCategory();
        rootCategory.setName(element);
        categoryRepository.save(rootCategory);
    }
}
