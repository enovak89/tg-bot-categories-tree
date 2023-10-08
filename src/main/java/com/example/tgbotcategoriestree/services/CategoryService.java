package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.Category;
import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.CategoryRepository;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.*;

/**
 * Service class to add, remove, check categories and formed map with all structured categories
 *
 * @author enovak89
 */
@Service
public class CategoryService {

    private final RootCategoryRepository rootCategoryRepository;
    private final ChildCategoryRepository childCategoryRepository;
    private final CategoryRepository categoryRepository;

    public CategoryService(RootCategoryRepository rootCategoryRepository, ChildCategoryRepository childCategoryRepository, CategoryRepository categoryRepository) {
        this.rootCategoryRepository = rootCategoryRepository;
        this.childCategoryRepository = childCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addRootElement(String element) {
        if (element == null || element.isEmpty()) {
            throw new IllegalArgumentException("The element can not be empty");
        }
        if (checkElementPresent(element)) {
            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before");
        }
        Category category = new Category();
        category.setName(element);
        categoryRepository.save(category);
    }

    public void addChildElement(String parentElement, String childElement) {
        if (parentElement == null || parentElement.isEmpty()) {
            throw new IllegalArgumentException("The parent element can not be empty");
        }
        if (childElement == null || childElement.isEmpty()) {
            throw new IllegalArgumentException("The child element can not be empty");
        }

        if (!checkElementPresent(parentElement)) {
            throw new IllegalArgumentException("The parent element \"" + parentElement + "\" was not found");
        }
        if (checkElementPresent(childElement)) {
            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before");
        }

        Category category = new Category();
        category.setParentCategory(categoryRepository.findByName(parentElement).get());
        category.setName(childElement);
        categoryRepository.save(category);
    }

    /**
     * Method to check root category present and save it or throws
     *
     * @param element - root category
     * @throws IllegalArgumentException
     */
//    public void addRootElement(String element) {
//        if (element == null || element.isEmpty()) {
//            throw new IllegalArgumentException("The element can not be empty");
//        }
//        if (checkRootElementPresent(element)) {
//            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before");
//        }
//        if (checkChildElementPresent(element)) {
//            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before as child");
//        }
//
//        RootCategory rootCategory = new RootCategory();
//        rootCategory.setName(element);
//        rootCategoryRepository.save(rootCategory);
//
//    }

    /**
     * Method to check root and child categories present and save it or throws
     *
     * @param rootElement  - root category
     * @param childElement - child category
     * @throws IllegalArgumentException
     */
//    public void addChildElement(String rootElement, String childElement) {
//        if (rootElement == null || rootElement.isEmpty()) {
//            throw new IllegalArgumentException("The root element can not be empty");
//        }
//        if (childElement == null || childElement.isEmpty()) {
//            throw new IllegalArgumentException("The child element can not be empty");
//        }
//
//        if (!checkRootElementPresent(rootElement) && !checkChildElementPresent(rootElement)) {
//            throw new IllegalArgumentException("The root element \"" + rootElement + "\" was not found");
//        }
//        if (checkChildElementPresent(childElement)) {
//            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before");
//        }
//        if (checkRootElementPresent(childElement)) {
//            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before as root");
//        }
////        if (checkChildElementPresent(rootElement)) {
////            throw new IllegalArgumentException("The root element \"" + rootElement + "\" has already been added before as child");
////        }
//
//        ChildCategory childCategory = new ChildCategory();
//        childCategory.setName(childElement);
//        childCategory.setRoot(rootCategoryRepository.findByName(rootElement).get());
//        childCategoryRepository.save(childCategory);
//
//    }

    /**
     * Method to check category present and remove it or throws
     *
     * @param element - category
     * @throws IllegalArgumentException
     */
    public void removeElement(String element) {

        if (checkElementPresent(element)) {
            Set<Category> childCategories = findChildCategories(element);
            if (!childCategories.isEmpty()) {
                childCategories
                        .forEach(category -> removeElement(category.getName()));
                removeElement(element);
            } else {
                categoryRepository.deleteByName(element);
            }
        } else {
            throw new IllegalArgumentException("The element \"" + element + "\" was not found");
        }
    }

    /**
     * Method to form map with all structured categories
     *
     * @return map with categories tree
     */
    public Map<String, List<String>> viewCategoriesTree() {

        return rootCategoryRepository.findAll().stream()
                .collect(
                        groupingBy(
                                RootCategory::getName,
                                flatMapping(root -> root.getChildCategories().stream()
                                        .map(ChildCategory::getName), toList())
                        ));
    }

    public String viewCategoriesTreeString() {
        StringBuilder result = new StringBuilder();
        Set<Category> setCategories = new TreeSet<>(Comparator.comparing(Category::getName));
        setCategories.addAll(categoryRepository.findAll());
        Set<Category> setRootCategories = new HashSet<>(categoryRepository.findAllByParentCategoryNameNull());

        setRootCategories
                .forEach(category -> {
                    result.append(category.getName()).append("\n");
                    AtomicReference<Integer> depth = new AtomicReference<>(1);
                    findChildCategories(category, setCategories, result, depth);
                });
        return result.toString();
    }

    public void findChildCategories(Category category, Set<Category> categorySet, StringBuilder result, AtomicReference<Integer> depth) {
        categorySet
                .forEach(childCategory -> {

                    if (childCategory.getParentCategory() != null && childCategory.getParentCategory().equals(category)) {

                        result.append(StringUtils.repeat("->", depth.getAndSet(depth.get() + 1)))
                                .append(childCategory.getName()).append("\n");
                        findChildCategories(childCategory, categorySet, result, depth);
                    }
                });
        depth.getAndSet(depth.get() - 1);
    }

    /**
     * Method to check root category present
     *
     * @param element - root category
     * @return boolean checking result
     */
    public boolean checkRootElementPresent(String element) {
        return rootCategoryRepository.findByName(element).isPresent();
    }

    /**
     * Method to check child category present
     *
     * @param element - child category
     * @return boolean checking result
     */
    public boolean checkChildElementPresent(String element) {
        return childCategoryRepository.findByName(element).isPresent();
    }

    public boolean checkElementPresent(String element) {
        return categoryRepository.findByName(element).isPresent();
    }

    public Set<Category> findChildCategories(String element) {
        return categoryRepository.findAllByParentCategoryName(element);
    }
}
