package test.files;


import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FilesTesing {
    private static final ClassLoader classLoader = FilesTesing.class.getClassLoader();
    private static final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * FileInputStream doesn't work with Zips only InputStreamReader
     *
     * @throws IOException
     */
    @Test
    void pdfFileTestShouldWorkCorrectly() throws IOException {
        InputStream is = classLoader.getResourceAsStream("zipfile.zip");
        if (is == null) {
            throw new FileNotFoundException("zipfile.zip not found in resources!");
        }

        int counter = 0;

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".pdf")) {
                    counter++;
                    BufferedReader buff = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));


                    StringBuilder builder = new StringBuilder();
                    int l;

                    while ((l = buff.read()) != -1) {
                        builder.append((char) l);

                    }


                    assertEquals("hello everyone", builder.toString());
                }

            }
            if (counter == 0) {
                fail("No pdf files found in zip archive. Total files processed:");
            }

        }
    }


    @Test
    void csvFileTestShouldWorkCorrectly() throws Exception {
        InputStream is = classLoader.getResourceAsStream("zipfile.zip");
        int counter = 0;
        if (is == null) {
            throw new FileNotFoundException("zipfile.zip not found in resources!");
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".csv")) {
                    counter++;
                    CSVReader buff = new CSVReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));


                    List<String[]> strings = buff.readAll();

                    strings.forEach(s -> System.out.println(Arrays.toString(s)));

                    assertArrayEquals(new String[]{"hello", "everyone"}, strings.get(0));
                    assertArrayEquals(new String[]{"my", " friend"}, strings.get(1));
                    assertEquals(2, strings.size());
                }
            }
            if (counter == 0) {
                fail("No CSV files found in zip archive. Total files processed:");
            }


        }
    }


    /**
     * xlsx формат нельзя сразу прочитать,поэтому считываем данные read(buffer) и записываем в массив байтов
     * ByteArrayOutputStream используя метод write(buffer, 0, len); откуда(buffer) и сколько байтов записывать до len
     *
     * @throws Exception
     */
    @Test
    void xlsxFileTestShouldWorkCorrectly() throws Exception {
        InputStream is = classLoader.getResourceAsStream("zipfile.zip");
        if (is == null) {
            throw new FileNotFoundException("zipfile.zip not found in resources!");
        }

        int counter = 0;
        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".xlsx")) {
                    counter++;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }


                    try (InputStream xlsxStream = new ByteArrayInputStream(baos.toByteArray())) {
                        XLS xls = new XLS(xlsxStream);


                        Sheet sheet = xls.excel.getSheetAt(0);


                        assertNotNull(sheet, "Sheet should not be null");

                        Row row = sheet.getRow(1);
                        assertNotNull(row, "Row 1 should not be null");

                        Cell cell = row.getCell(1);
                        assertNotNull(cell, "Cell at row 1, column 1 should not be null");


                        String stringCellValue1 = cell.getStringCellValue();

                        assertNotNull(stringCellValue1);

                        Assertions.assertAll(
                                () -> assertTrue(stringCellValue1.contains("eatable"),
                                        "Should contain 'eatable'"),
                                () -> assertTrue(stringCellValue1.contains("Kukuruza"),
                                        "Should contain 'Kukuruza'")
                        );


                    }

                }

            }

            if (counter == 0) {
                fail("No xlsx files found in zip archive. Total files processed:");
            }
        }
    }


    /**
     * ObjectMapper.convertValue()
     * Конвертация между Java-объектами (не из JSON!)
     * <p>
     * readValue() - десериализация json
     *
     * @throws Exception
     */
    @Test
    void jsonFileParsingTest() throws Exception {
        InputStream is = classLoader.getResourceAsStream("zipfile.zip");
        if (is == null) {
            throw new FileNotFoundException("zipfile.zip not found in resources!");
        }

        int counter = 0;
        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;


            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".json")) {
                    counter++;
                    Person person = objectMapper.readValue(zipInputStream, Person.class);

                    assertAll(() -> assertEquals("John Doe", person.getName()), () -> assertEquals(30, person.getAge()),
                            () -> assertFalse(person.isStudent()), () -> assertArrayEquals(new String[]{"Math", "Science"}, person.getCourses()), () ->
                                    assertEquals(new LocationPoint("123 Main St", "Anytown", "12345"), person.getAddress()));
                    break;
                }
            }

            if (counter == 0) {
                fail("No json files found in zip archive. Total files processed:");
            }
        }

    }
}