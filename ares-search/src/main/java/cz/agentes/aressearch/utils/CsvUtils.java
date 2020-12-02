package cz.agentes.aressearch.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {

    public static List<String[]> readCsv(String sourceFile, String splitChar) throws IOException {
        File file = new File(sourceFile);
        List<String[]> content = new ArrayList<>();

        if (file.isFile()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
            String row;
            while ((row = csvReader.readLine()) != null) {
                content.add(row.split(splitChar));
            }

            csvReader.close();
        }

        return content;
    }

    public static List<String> readCsvRows(String sourceFile, String splitChar) throws IOException {
        File file = new File(sourceFile);
        List<String> content = new ArrayList<>();

        if (file.isFile()) {
            BufferedReader csvReader = new BufferedReader(new FileReader(file));
            String row;
            while ((row = csvReader.readLine()) != null) {
                content.add(row);
            }

            csvReader.close();
        }

        return content;
    }

    public static void writeCsv(String targetFile, List<List<String>> content, String splitChar) throws IOException {
        File file = new File(targetFile);
        FileWriter csvWriter = new FileWriter(targetFile);

        for(List<String> row : content) {
            csvWriter.append(String.join(splitChar, row));
            csvWriter.append("\n");
        }

        csvWriter.flush();;
        csvWriter.close();
    }
}
