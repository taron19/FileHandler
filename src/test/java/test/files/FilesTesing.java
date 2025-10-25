package test.files;


import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
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

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".pdf")) {
                    BufferedReader buff = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));


                    StringBuilder builder = new StringBuilder();
                    int l;

                    while ((l = buff.read()) != -1) {
                        builder.append((char) l);

                    }


                    assertEquals("hello everyone", builder.toString());
                }

            }

        }
    }


    @Test
    void csvFileTestShouldWorkCorrectly() throws Exception {
        InputStream is = classLoader.getResourceAsStream("zipfile.zip");
        if (is == null) {
            throw new FileNotFoundException("zipfile.zip not found in resources!");
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".csv")) {
                    CSVReader buff = new CSVReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));


                    List<String[]> strings = buff.readAll();

                    strings.forEach(s -> System.out.println(Arrays.toString(s)));

                    assertArrayEquals(new String[]{"hello everyone"}, strings.get(0));
                    assertArrayEquals(new String[]{"my friend"}, strings.get(1));
                    assertEquals(2, strings.size());
                }

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

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".xlsx")) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = zipInputStream.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }


                    try (InputStream xlsxStream = new ByteArrayInputStream(baos.toByteArray())) {
                        XLS xls = new XLS(xlsxStream);


                        String stringCellValue = xls.excel
                                .getSheetAt(0)
                                .getRow(1)
                                .getCell(1)
                                .getStringCellValue();


                        assertNotNull(stringCellValue);
                        Assertions.assertAll(() -> stringCellValue.contains("eatable"), () -> stringCellValue.contains("Kukuruza"));


                    }

                }

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

        try (ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;


            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".json")) {

                    Person person = objectMapper.readValue(zipInputStream, Person.class);

                    assertAll(() -> assertEquals("John Doe", person.getName()), () -> assertEquals(30, person.getAge()),
                            () -> assertFalse(person.isStudent()), () -> assertArrayEquals(new String[]{"Math", "Science"}, person.getCourses()), () ->
                                    assertEquals(new LocationPoint("123 Main St", "Anytown", "12345"), person.getAddress()));
                    break;
                }
            }
        }

    }
}