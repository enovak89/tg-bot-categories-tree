package com.example.tgbotcategoriestree.services;

import org.apache.commons.lang3.StringUtils;
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
        Workbook excelBookCategories = recordDataInWorkbookFromDbString();

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
    private Workbook recordDataInWorkbookFromDbString() {
        //Getting all categories from DB
        String categoriesTreeString = categoryService.viewCategoriesTree();
        String[] categoriesTreeStringArray = StringUtils.split(categoriesTreeString, '\n');

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet excelSheetCategories = workbook.createSheet("Categories Tree");
        CellStyle cellStyle = createCellStyle(workbook);

        for (int i = 0; i < categoriesTreeStringArray.length; i++) {
            Row row = excelSheetCategories.createRow(i);
            int cellNumber = StringUtils.countMatches(categoriesTreeStringArray[i], categoryService.getSeparatorSymbol());
            String category = categoriesTreeStringArray[i].substring(cellNumber);
            Cell cell = row.createCell(cellNumber);
            cell.setCellValue(category);
            cell.setCellStyle(cellStyle);
        }
        return workbook;
    }

    /**
     * Method to create Excel cells style
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

        if (!sameElements.isEmpty()) {
            throw new IllegalArgumentException("Uploading is completed unsuccessfully. " +
                    "\nThe following elements have already been added to categories tree before: " + sameElements
                    + "\nDelete it and try again");
        }

        Sheet firstSheet = workbook.getSheetAt(0);
        StringBuilder parentCategory = new StringBuilder();

        for (int i = 0; i <= firstSheet.getLastRowNum(); i++) {
            Row row = firstSheet.getRow(i);
            int lastCellNumber = row.getLastCellNum();

            if (lastCellNumber == 1) {
                //When row contains root category

                parentCategory.replace(0, parentCategory.length(), row.getCell(0).toString());
                categoryService.addRootElement(parentCategory.toString());

            } else {
                //When row not contains root category

                int previousRowsLastCellNumber = firstSheet.getRow(i - 1).getLastCellNum();

                if (lastCellNumber - previousRowsLastCellNumber > 0) {

                    parentCategory.replace(0, parentCategory.length(),
                            firstSheet.getRow(i - 1).getCell(previousRowsLastCellNumber - 1).toString());

                } else if (lastCellNumber - previousRowsLastCellNumber < 0) {

                    for (int j = i - 1; j >= 0; j--) {

                        int previousRowWithParentCellNumber = firstSheet.getRow(j).getLastCellNum();

                        if (lastCellNumber - 1 == previousRowWithParentCellNumber) {
                            parentCategory.replace(0, parentCategory.length(),
                                    firstSheet.getRow(j).getCell(previousRowWithParentCellNumber - 1).toString());
                            break;
                        }
                    }
                }

                categoryService.addChildElement(parentCategory.toString(),
                        row.getCell(lastCellNumber - 1).toString());

            }
        }
    }

    /**
     * Method to check already added categories in DB and return its
     *
     * @param workbook from user message
     * @return set with the same elements in user workbook and DB
     */
    private Set<String> checkDataInWorkBook(Workbook workbook) {
        //Getting string with all categories
        String stringCategoriesFromDb = categoryService.viewCategoriesTree();

        Set<String> sameElements = new HashSet<>();

        //Checking DB contains categories from user workbook
        Sheet firstSheet = workbook.getSheetAt(0);
        for (int i = 0; i <= firstSheet.getLastRowNum(); i++) {
            Row row = firstSheet.getRow(i);
            if (row == null) {
                throw new IllegalArgumentException("Uploading is completed unsuccessfully." +
                        "\nThere cannot be empty rows");
            }
            for (int j = 0; j < row.getLastCellNum(); j++) {
                if (row.getCell(j) == null || row.getCell(j).toString().isEmpty()) {
                    continue;
                }
                if (stringCategoriesFromDb.contains(row.getCell(j).toString())) {
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
