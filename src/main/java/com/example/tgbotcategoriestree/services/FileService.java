package com.example.tgbotcategoriestree.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service class for operation with xlsx files
 *
 * @author enovak89
 */
@Service
public class FileService {

    private final static String FILE_NAME = "bookCategoriesTree.xlsx";

    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final CategoryService categoryService;

    public FileService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Method to save xlsx file containing DB data in root directory and create its inputStream
     *
     * @return FileInputStream with xlsx file
     * @throws FileNotFoundException
     */
    public FileInputStream createWorkBook() throws FileNotFoundException {
        //Getting styled workBook with all categories
        Workbook excelBookCategories = recordDataInWorkbookFromDb();

        //Creating and saving file
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + FILE_NAME;

        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            excelBookCategories.write(outputStream);
            excelBookCategories.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new FileInputStream(fileLocation);
    }

    /**
     * Method to create excel workBook with categories from DB
     *
     * @return workBook with data
     */
    private Workbook recordDataInWorkbookFromDb() {
        //Getting all categories from DB
        Map<String, List<String>> mapCategories = categoryService.viewCategoriesTree();

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet excelSheetCategories = workbook.createSheet("Categories Tree");

        CellStyle cellStyle = createCellStyle(workbook);

        int rowNumber = 0;
        int cellNumber;
        int maxColumnNumber = 0;

        //Recording categories to excel workBook
        //Root - in the first cell, child - in the next cells in row
        for (String root : mapCategories.keySet()) {
            Row row = excelSheetCategories.createRow(rowNumber++);
            Cell cellRoot = row.createCell(0);
            cellRoot.setCellValue(root);
            cellRoot.setCellStyle(cellStyle);
            cellNumber = 1;
            for (String child : mapCategories.get(root)) {
                Cell cellChild = row.createCell(cellNumber++);
                cellChild.setCellValue(child);
                cellChild.setCellStyle(cellStyle);
                maxColumnNumber = Math.max(maxColumnNumber, cellNumber);
            }
        }

        for (int i = 0; i < maxColumnNumber; i++) {
            excelSheetCategories.autoSizeColumn(i);
        }

        return workbook;
    }

    /**
     * Method to create excel cells style
     *
     * @param workbook for cells styling
     * @return cellStyle with Ground color and font
     */
    private CellStyle createCellStyle(Workbook workbook) {

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        cellStyle.setFont(font);

        return cellStyle;
    }

    /**
     * Method to record categories from excel workBook into DB
     *
     * @param workbook from user message
     * @throws IllegalArgumentException
     */
    public void recordDataInDbFromWorkBook(Workbook workbook) {

        Set<String> sameElements = checkDataInWorkBook(workbook);

        if (sameElements.isEmpty()) {

            Sheet firstSheet = workbook.getSheetAt(0);
            for (int i = 0; i <= firstSheet.getLastRowNum(); i++) {
                Row row = firstSheet.getRow(i);
                String rootCategoryName = row.getCell(0).toString();
                categoryService.addRootElement(rootCategoryName);
                for (int j = 1; j < row.getLastCellNum(); j++) {
                    categoryService.addChildElement(rootCategoryName, row.getCell(j).toString());
                }
            }
        } else {
            throw new IllegalArgumentException("Downloading is completed unsuccessfully. " +
                    "\nThe following elements have already been added to categories tree before: " + sameElements
                    + "\nDelete it and try again");
        }
    }

    /**
     * Method to check already added categories in DB and return its
     *
     * @param workbook from user message
     * @return set with the same elements in user workbook and DB
     */
    private Set<String> checkDataInWorkBook(Workbook workbook) {
        //Getting map with all categories
        Map<String, List<String>> mapCategoriesFromDb = categoryService.viewCategoriesTree();
        Set<String> setCategoriesFromDb = new HashSet<>();
        Set<String> sameElements = new HashSet<>();

        //Mapping to set
        for (String root : mapCategoriesFromDb.keySet()) {
            setCategoriesFromDb.add(root);
            setCategoriesFromDb.addAll(mapCategoriesFromDb.get(root));
        }

        //Checking DB contains categories from user workbook
        Sheet firstSheet = workbook.getSheetAt(0);
        for (int i = 0; i <= firstSheet.getLastRowNum(); i++) {
            Row row = firstSheet.getRow(i);
            String rootCategoryName = row.getCell(0).toString();
            if (setCategoriesFromDb.contains(rootCategoryName)) {
                sameElements.add(rootCategoryName);
            }
            for (int j = 1; j < row.getLastCellNum(); j++) {
                if (setCategoriesFromDb.contains(row.getCell(j).toString())) {
                    sameElements.add(row.getCell(j).toString());
                }
            }
        }
        return sameElements;
    }

    /**
     * Getter for file name constant
     *
     * @return file name
     */
    public String getFileName() {
        return FILE_NAME;
    }
}
