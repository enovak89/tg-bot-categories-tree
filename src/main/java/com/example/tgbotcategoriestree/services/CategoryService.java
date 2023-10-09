package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.Category;
import com.example.tgbotcategoriestree.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service class to add, remove, check categories and formed map with all structured categories
 *
 * @author enovak89
 */
@Service
public class CategoryService {

    private final static String SEPARATOR_SYMBOL = ">";

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Method to check root category present and save it or throws
     *
     * @param element - root category
     * @throws IllegalArgumentException
     */
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

    /**
     * Method to check root and child categories present and save it or throws
     *
     * @param parentElement - parent category
     * @param childElement  - child category
     * @throws IllegalArgumentException
     */
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
     * Method to check category present and remove it or throws
     *
     * @param element - category
     * @throws IllegalArgumentException
     */
    public void removeElement(String element) {

        if (checkElementPresent(element)) {
            Set<Category> childCategories = categoryRepository.findAllByParentCategoryName(element);
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
     * Method to form string with all structured categories
     *
     * @return string with categories tree
     */
    public String viewCategoriesTree() {
        StringBuilder result = new StringBuilder();
        //Getting set with all categories from DB
        Set<Category> setCategories = new TreeSet<>(Comparator.comparing(Category::getName));
        setCategories.addAll(categoryRepository.findAll());

        //Getting set with all root categories from DB
        Set<Category> setRootCategories = new TreeSet<>(Comparator.comparing(Category::getName));
        setRootCategories.addAll(categoryRepository.findAllByParentCategoryNameNull());

        setRootCategories
                .forEach(category -> {
                    result.append(category.getName()).append("\n");
                    AtomicReference<Integer> depth = new AtomicReference<>(1);
                    findChildCategories(category, setCategories, result, depth);
                });
        return result.toString();
    }

    /**
     * Method to find child categories of element
     *
     * @param category    - parent category
     * @param categorySet - all categories
     * @param result      - string with finded categories
     * @param depth       - level of inheritance
     * @return set with child categories
     */
    public void findChildCategories(Category category, Set<Category> categorySet,
                                    StringBuilder result, AtomicReference<Integer> depth) {
        categorySet
                .forEach(childCategory -> {

                    if (childCategory.getParentCategory() != null && childCategory.getParentCategory().equals(category)) {
                    //When category is no root and is child for parameter method category
                        result.append(SEPARATOR_SYMBOL.repeat(depth.getAndSet(depth.get() + 1)))
                                .append(childCategory.getName()).append("\n");
                        findChildCategories(childCategory, categorySet, result, depth);
                    }
                });
        depth.getAndSet(depth.get() - 1);
    }

    /**
     * Method to check category present
     *
     * @param element - category
     * @return boolean checking result
     */
    private boolean checkElementPresent(String element) {
        return categoryRepository.findByName(element).isPresent();
    }

    /**
     * Method to get separator symbol
     *
     * @return separator symbol
     */
    public String getSeparatorSymbol() {
        return SEPARATOR_SYMBOL;
    }
}
