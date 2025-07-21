package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FilesTest {
    private ClassLoader cl = FilesTest.class.getClassLoader();

    @Test
    @DisplayName("Проверка CSV-файла из ZIP архива")
    void csvFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(cl.getResourceAsStream("fill.zip"))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".csv")) {
                    try (CSVReader csvReader = new CSVReader(new InputStreamReader(zis))) {
                        List<String[]> data = csvReader.readAll();
                        Assertions.assertEquals("Customer ID", data.get(0)[0]);
                        Assertions.assertEquals(4, data.size());
                        Assertions.assertArrayEquals(new String[]{"35", "151", "8796712009", "2341543509824323"}, data.get(1));
                    }
                    break;
                }

            }
        }

    }

    @Test
    @DisplayName("Проверка PDF-файла из ZIP архива")
    void pdfFileParsingTest() throws Exception {
        try (ZipInputStream zipInput = new ZipInputStream(
                cl.getResourceAsStream("fill.zip")
        )) {
            ZipEntry entry;
            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().endsWith(".pdf")) {
                    PDF pdf = new PDF(zipInput);
                    Assertions.assertEquals("DNS – интернет магазин цифровой и бытовой техники по доступным ценам.", pdf.title);
                    Assertions.assertEquals(null, pdf.author);
                    return;
                }
            }
        }
    }

    @Test
    @DisplayName("Проверка XLSX-файла из ZIP архива")
    void xlsxFileParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("fill.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().endsWith(".xlsx")) {
                    XLS xls = new XLS(zis);
                    String actualValue = xls.excel.getSheetAt(0).getRow(0).getCell(3).getStringCellValue();
                    Assertions.assertTrue(actualValue.contains("Postal code"));
                }
            }
        }
    }

    @Test
    @DisplayName("Проверка Json-файла")
    void dnsReserveJsonShouldBeValidTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = cl.getResourceAsStream("DNS.json")) {
            JsonNode root = mapper.readTree(is);
            JsonNode dnsReserve = root.get("dnsReserve");

            Assertions.assertEquals("ORD-20250714-001", dnsReserve.get("orderId").asText());

            JsonNode customer = dnsReserve.get("customer");
            Assertions.assertEquals("Иван Петров", customer.get("name").asText());
            Assertions.assertEquals("ivan.petrov@example.com", customer.get("email").asText());
            Assertions.assertEquals("+7-900-123-45-67", customer.get("phone").asText());

            JsonNode items = dnsReserve.get("items");
            JsonNode firstItem = items.get(0);
            Assertions.assertEquals("SKU-12345", firstItem.get("productId").asText());
            Assertions.assertEquals("Беспроводная мышь Logitech M185", firstItem.get("name").asText());
            Assertions.assertEquals(1, firstItem.get("quantity").asInt());
            Assertions.assertEquals(1299.00, firstItem.get("price").asDouble());

            JsonNode secondItem = items.get(1);
            Assertions.assertEquals("SKU-67890", secondItem.get("productId").asText());
            Assertions.assertEquals("Клавиатура Logitech K380", secondItem.get("name").asText());
            Assertions.assertEquals(2, secondItem.get("quantity").asInt());
            Assertions.assertEquals(2890.00, secondItem.get("price").asDouble());

            Assertions.assertEquals(7079.00, dnsReserve.get("total").asDouble());

            JsonNode delivery = dnsReserve.get("delivery");
            Assertions.assertEquals("Курьер", delivery.get("type").asText());
            Assertions.assertEquals("г. Москва, ул. Ленина, д. 10, кв. 15", delivery.get("address").asText());
            Assertions.assertEquals("2025-07-16", delivery.get("date").asText());

            Assertions.assertEquals("Ожидает отправки", dnsReserve.get("status").asText());
        }
    }
}





