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
import java.util.List;
import java.util.Map;

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
     * Method to save xlsx file in root directory and create its inputStream
     * @return FileInputStream with xlsx file
     */
    public FileInputStream createWorkBook() throws FileNotFoundException {
        //Getting styled workBook with all categories
        Workbook excelBookCategories = recordDataInWorkbookFromDb();

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
     * @param workbook
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
     * Getter for file name constant
     */
    public String getFileName() {
        return FILE_NAME;
    }
}
