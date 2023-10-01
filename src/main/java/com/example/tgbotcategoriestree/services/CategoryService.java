package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

        if (findRootElement(element)) {
            throw new IllegalArgumentException("The element has already been added before");
        }
        if (findChildElement(element)) {
            throw new IllegalArgumentException("The element has already been added before as child");
        }

        RootCategory rootCategory = new RootCategory();
        rootCategory.setName(element);
        rootCategoryRepository.save(rootCategory);

    }

    public void addChildElement(String rootElement, String childElement) {

        if (!findRootElement(rootElement)) {
            throw new IllegalArgumentException("The root element was not found");
        }
        if (findChildElement(childElement)) {
            throw new IllegalArgumentException("The child element has already been added before");
        }
        if (findRootElement(childElement)) {
            throw new IllegalArgumentException("The child element has already been added before as root");
        }
        if (findChildElement(rootElement)) {
            throw new IllegalArgumentException("The root element has already been added before as child");
        }

        ChildCategory childCategory = new ChildCategory();
        childCategory.setName(childElement);
        childCategory.setRoot(rootCategoryRepository.findByName(rootElement).get());
        childCategoryRepository.save(childCategory);

    }

    public Map<String, List<String>> viewCategoriesTree() {
        Map<String, List<String>> categoriesTree = new TreeMap<>();
        List<RootCategory> rootCategories = rootCategoryRepository.findAll();
        return categoriesTree = rootCategories.stream()
                .collect(groupingBy(RootCategory::getName,
                        mapping(RootCategory::getName, toList())));


//        Map<City, Set<String>> namesByCity    = people.stream().collect(
//                groupingBy(Person::getCity,
//                        mapping(Person::getLastName,
//                                toSet())));


//                        root -> List.of(childCategoryRepository.findAllByRootId(1L))));


//        Collectors.toList(childCategoryRepository.findAllByRootId(1L))


//        categoriesTree.forEach((key, value) -> {
//            viewMessageBuilder.append(key).append("\n");
//            value
//                    .forEach(child -> viewMessageBuilder.append("  -").append(child).append("\n"));
//        });
    }

    public boolean findRootElement(String element) {
        return rootCategoryRepository.findByName(element).isPresent();
    }

    public boolean findChildElement(String element) {
        return childCategoryRepository.findByName(element).isPresent();
    }
}
