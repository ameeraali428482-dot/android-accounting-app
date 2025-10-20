package com.example.androidapp.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    private static final String TAG = "ExcelUtil";

    // Method to import data from CSV file
    public static List<List<String>> importDataFromCSV(Context context, Uri uri) {
        List<List<String>> data = new ArrayList<>();
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {
            
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                List<String> row = new ArrayList<>();
                for (String value : nextLine) {
                    row.add(value);
                }
                data.add(row);
            }
            
            Log.d(TAG, "Successfully imported " + data.size() + " rows from CSV");
        } catch (Exception e) {
            Log.e(TAG, "Error importing data from CSV", e);
        }
        return data;
    }

    // Method to export data to CSV file
    public static boolean exportDataToCSV(Context context, Uri uri, List<List<String>> data, String fileName) {
        try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
             OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            for (List<String> row : data) {
                String[] rowArray = row.toArray(new String[0]);
                csvWriter.writeNext(rowArray);
            }
            
            Log.d(TAG, "Successfully exported " + data.size() + " rows to CSV");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error exporting data to CSV", e);
            return false;
        }
    }

    // Method to export data with headers
    public static boolean exportDataWithHeaders(Context context, Uri uri, List<String> headers, List<List<String>> data) {
        List<List<String>> allData = new ArrayList<>();
        allData.add(headers);
        allData.addAll(data);
        return exportDataToCSV(context, uri, allData, "export");
    }
}
