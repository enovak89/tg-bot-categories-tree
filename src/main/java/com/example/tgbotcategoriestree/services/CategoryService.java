package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.CategoryRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@NoArgsConstructor(force = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void addRootElement(String element) {
        RootCategory rootCategory = new RootCategory();
        rootCategory.setName(element);
        System.out.println("here");
//        categoryRepository.save(rootCategory);
    }
}
