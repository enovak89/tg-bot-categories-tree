package com.example.tgbotcategoriestree.services;

import com.example.tgbotcategoriestree.models.ChildCategory;
import com.example.tgbotcategoriestree.models.RootCategory;
import com.example.tgbotcategoriestree.repository.ChildCategoryRepository;
import com.example.tgbotcategoriestree.repository.RootCategoryRepository;
import com.example.tgbotcategoriestree.updatesHandlers.CommandHandler;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
public class CategoryService {

    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
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

    public void removeElement(String element) {

        if (findChildElement(element)) {
            childCategoryRepository.deleteByName(element);
        } else if (findRootElement(element)) {
            rootCategoryRepository.deleteByName(element);
        } else {
            throw new IllegalArgumentException("The element was not found");
        }
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

    public void writeInWorkbookFromDb() {
        Map<String, List<String>> mapCategories = viewCategoriesTree();

        Workbook excelBookCategories = new XSSFWorkbook();
        Sheet excelSheetCategories = excelBookCategories.createSheet("Categories Tree");

        CellStyle cellStyle = excelBookCategories.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) excelBookCategories).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        cellStyle.setFont(font);

        Integer rowNumber = 0;
        Integer cellNumber = 1;
        Integer columnNumber = 0;

        for (String root : mapCategories.keySet()) {
            Row row = excelSheetCategories.createRow(rowNumber++);
            System.out.println("rowNumber = " + rowNumber);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(root);
            cellNumber = 1;
            for (String child : mapCategories.get(root)) {
                row.createCell(cellNumber++).setCellValue(child);
                System.out.println("cellNumber = " + cellNumber);
                columnNumber = columnNumber < cellNumber ? cellNumber : columnNumber;
            }
        }

        System.out.println("columnNumber = " + columnNumber);

        for (int i = 0; i < excelSheetCategories.getLastRowNum() - 1; i++) {
            System.out.println("___________");
            for (int j = 0; j < excelSheetCategories.getRow(i).getLastCellNum() - 1; j++) {
                System.out.print(excelSheetCategories.getRow(i).getCell(j) + " | ");
            }
        }

        for (int i = 0; i < columnNumber - 1; i++) {
            excelSheetCategories.autoSizeColumn(i);
        }


        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            excelBookCategories.write(outputStream);
            excelBookCategories.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

//        try {
//            FileOutputStream fileOut = new FileOutputStream("BookCategoriesTree.xlsx");
//            excelBookCategories.write(fileOut);
//            fileOut.close();
//        }
//        catch (Exception e) {
//            logger.error(e.getMessage());
//        }
    }

    public boolean findRootElement(String element) {
        return rootCategoryRepository.findByName(element).isPresent();
    }

    public boolean findChildElement(String element) {
        return childCategoryRepository.findByName(element).isPresent();
    }
}
