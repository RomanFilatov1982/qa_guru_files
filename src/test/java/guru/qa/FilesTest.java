package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

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
                        Assertions.assertThat(data.get(0)[0]).isEqualTo("Customer ID");
                        Assertions.assertThat(data.size()).isEqualTo(4);
                        Assertions.assertThat(data.get(1)).isEqualTo(new String[]{"35", "151", "8796712009", "2341543509824323"});
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
                    Assertions.assertThat(pdf.title).isEqualTo("DNS – интернет магазин цифровой и бытовой техники по доступным ценам.");
                    assertThat(pdf.author).isNull();
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
                    Assertions.assertThat(actualValue).contains("Postal code");
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

            Assertions.assertThat(dnsReserve.get("orderId").asText()).isEqualTo("ORD-20250714-001");

            JsonNode customer = dnsReserve.get("customer");
            Assertions.assertThat(customer.get("name").asText()).isEqualTo("Иван Петров");
            Assertions.assertThat(customer.get("email").asText()).isEqualTo("ivan.petrov@example.com");
            Assertions.assertThat(customer.get("phone").asText()).isEqualTo("+7-900-123-45-67");

            JsonNode items = dnsReserve.get("items");
            JsonNode firstItem = items.get(0);
            Assertions.assertThat(firstItem.get("productId").asText()).isEqualTo("SKU-12345");
            Assertions.assertThat(firstItem.get("name").asText()).isEqualTo("Беспроводная мышь Logitech M185");
            Assertions.assertThat(firstItem.get("quantity").asInt()).isEqualTo(1);
            Assertions.assertThat(firstItem.get("price").asDouble()).isEqualTo(1299.00);

            JsonNode secondItem = items.get(1);
            Assertions.assertThat(secondItem.get("productId").asText()).isEqualTo("SKU-67890");
            Assertions.assertThat(secondItem.get("name").asText()).isEqualTo("Клавиатура Logitech K380");
            Assertions.assertThat(secondItem.get("quantity").asInt()).isEqualTo(2);
            Assertions.assertThat(secondItem.get("price").asDouble()).isEqualTo(2890.00);

            Assertions.assertThat(dnsReserve.get("total").asDouble()).isEqualTo(7079.00);

            JsonNode delivery = dnsReserve.get("delivery");
            Assertions.assertThat(delivery.get("type").asText()).isEqualTo("Курьер");
            Assertions.assertThat(delivery.get("address").asText()).isEqualTo("г. Москва, ул. Ленина, д. 10, кв. 15");
            Assertions.assertThat(delivery.get("date").asText()).isEqualTo("2025-07-16");

            Assertions.assertThat(dnsReserve.get("status").asText()).isEqualTo("Ожидает отправки");
        }
    }
}





