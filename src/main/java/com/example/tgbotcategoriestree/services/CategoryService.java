package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public CategoryService(RootCategoryRepository rootCategoryRepository, ChildCategoryRepository childCategoryRepository) {
        this.rootCategoryRepository = rootCategoryRepository;
        this.childCategoryRepository = childCategoryRepository;
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
        if (checkRootElementPresent(element)) {
            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before");
        }
        if (checkChildElementPresent(element)) {
            throw new IllegalArgumentException("The element \"" + element + "\" has already been added before as child");
        }

        RootCategory rootCategory = new RootCategory();
        rootCategory.setName(element);
        rootCategoryRepository.save(rootCategory);

    }

    /**
     * Method to check root and child categories present and save it or throws
     *
     * @param rootElement  - root category
     * @param childElement - child category
     * @throws IllegalArgumentException
     */
    public void addChildElement(String rootElement, String childElement) {
        if (rootElement == null || rootElement.isEmpty()) {
            throw new IllegalArgumentException("The root element can not be empty");
        }
        if (childElement == null || childElement.isEmpty()) {
            throw new IllegalArgumentException("The child element can not be empty");
        }

        if (!checkRootElementPresent(rootElement)) {
            throw new IllegalArgumentException("The root element \"" + rootElement + "\" was not found");
        }
        if (checkChildElementPresent(childElement)) {
            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before");
        }
        if (checkRootElementPresent(childElement)) {
            throw new IllegalArgumentException("The child element \"" + childElement + "\" has already been added before as root");
        }
        if (checkChildElementPresent(rootElement)) {
            throw new IllegalArgumentException("The root element \"" + rootElement + "\" has already been added before as child");
        }

        ChildCategory childCategory = new ChildCategory();
        childCategory.setName(childElement);
        childCategory.setRoot(rootCategoryRepository.findByName(rootElement).get());
        childCategoryRepository.save(childCategory);

    }

    /**
     * Method to check category present and remove it or throws
     *
     * @param element - category
     * @throws IllegalArgumentException
     */
    public void removeElement(String element) {

        if (checkChildElementPresent(element)) {
            childCategoryRepository.deleteByName(element);
        } else if (checkRootElementPresent(element)) {
            rootCategoryRepository.deleteByName(element);
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
}
