package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
public class CategoryService {

    private final RootCategoryRepository rootCategoryRepository;
    private final ChildCategoryRepository childCategoryRepository;

    public CategoryService(RootCategoryRepository rootCategoryRepository, ChildCategoryRepository childCategoryRepository) {
        this.rootCategoryRepository = rootCategoryRepository;
        this.childCategoryRepository = childCategoryRepository;
    }

    public void addRootElement(String element) {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("The element can not be empty");
        }
        if (findRootElement(element)) {
            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before");
        }
        if (findChildElement(element)) {
            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before as child");
        }

        RootCategory rootCategory = new RootCategory();
        rootCategory.setName(element);
        rootCategoryRepository.save(rootCategory);

    }

    public void addChildElement(String rootElement, String childElement) {
        if (rootElement == null || rootElement.isEmpty()) {
            throw new IllegalArgumentException("The root element can not be empty");
        }
        if (childElement == null || childElement.isEmpty()) {
            throw new IllegalArgumentException("The child element can not be empty");
        }

        if (!findRootElement(rootElement)) {
            throw new IllegalArgumentException("The root element \"" + rootElement + "\" was not found");
        }
        if (findChildElement(childElement)) {
            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before");
        }
        if (findRootElement(childElement)) {
            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before as root");
        }
        if (findChildElement(rootElement)) {
            throw new IllegalArgumentException("The root element \"" + rootElement + "\" has already been added before as child");
        }

        ChildCategory childCategory = new ChildCategory();
        childCategory.setName(childElement);
        childCategory.setRoot(rootCategoryRepository.findByName(rootElement).get());
        childCategoryRepository.save(childCategory);

    }

    public void removeElement(String element) {

        if (findChildElement(element)) {
            childCategoryRepository.deleteByName(element);
        } else if (findRootElement(element)) {
            rootCategoryRepository.deleteByName(element);
        } else {
            throw new IllegalArgumentException("The element \"" + element + "\" was not found");
        }
    }

    public void removeAllElements() {
        rootCategoryRepository.deleteAll();
        childCategoryRepository.deleteAll();
    }

    public Map<String, List<String>> viewCategoriesTree() {

        return rootCategoryRepository.findAll().stream()
                .collect(
                        groupingBy(
                                RootCategory::getName,
                                flatMapping(root -> root.getChildCategories().stream()
                                        .map(ChildCategory::getName), toList())
                        ));
    }

    public boolean findRootElement(String element) {
        return rootCategoryRepository.findByName(element).isPresent();
    }

    public boolean findChildElement(String element) {
        return childCategoryRepository.findByName(element).isPresent();
    }
}
