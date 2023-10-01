package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final RootCategoryRepository rootCategoryRepository;
    private final ChildCategoryRepository childCategoryRepository;

    public CategoryService(RootCategoryRepository rootCategoryRepository, ChildCategoryRepository childCategoryRepository) {
        this.rootCategoryRepository = rootCategoryRepository;
        this.childCategoryRepository = childCategoryRepository;
    }

    public void addRootElement(String element) {
        if (rootCategoryRepository.findByName(element).isEmpty()) {
            RootCategory rootCategory = new RootCategory();
            rootCategory.setName(element);
            rootCategoryRepository.save(rootCategory);
        } else {
            throw new IllegalArgumentException("Element has already been added before");
        }
    }

    public void addChildElement(String rootElement, String childElement) {
        if (true) {
            System.out.println("no");
        } else {
            System.out.println("yes");
        }
    }

}
