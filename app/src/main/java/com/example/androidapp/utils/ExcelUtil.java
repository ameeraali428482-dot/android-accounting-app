package com.example.androidapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





public class ExcelUtil {

    private static final String TAG = "ExcelUtil";

    // Method to import data from an Excel file (XLSX format)
    public static List<List<String>> importDataFromExcel(Context context, Uri uri) {
        List<List<String>> data = new ArrayList<>();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Get first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                List<String> rowData = new ArrayList<>();
                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    rowData.add(getCellValue(cell));
                }
                data.add(rowData);
            }
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Error importing data from Excel", e);
        }
        return data;
    }

    // Helper method to get cell value as string
    private static String getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    // Method to export data to an Excel file (XLSX format)
    public static boolean exportDataToExcel(Context context, Uri uri, List<List<String>> data, String sheetName) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);

            int rowNum = 0;
            for (List<String> rowData : data) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String cellData : rowData) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(cellData);
                }
            }

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error exporting data to Excel", e);
            return false;
        }
    }
}
