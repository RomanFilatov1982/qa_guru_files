package guru.qa;

import com.codeborne.pdftest.PDF;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jxls.reader.XLSReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Pdf;
import org.openqa.selenium.json.Json;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class FilesTest {
    private ClassLoader cl = FilesTest.class.getClassLoader();


    @Test
    void unZipFileAndReadAll() throws Exception {
        try (InputStream is = cl.getResourceAsStream("fill.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                byte[] bytes = zis.readAllBytes();
                if (name.equals("1.pdf")) {
                    try (PDDocument pdf = PDDocument.load(new ByteArrayInputStream(bytes))) {
                        String text = new PDFTextStripper().getText(pdf);
                        Assertions.assertTrue(text.contains("Блок питания DEEPCOOL PF500 [R-PF500D-HA0B-EU] черный"));

                    }
                } else if (name.equals("2.xlsx")) {
                    try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
                        Sheet sheet = workbook.getSheetAt(0);
                        Assertions.assertEquals("февраль07", sheet.getSheetName());

                    }
                } else if (name.equals("3.csv")) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))) {
                        List<String> lines = reader.lines().collect(Collectors.toList());
                        Assertions.assertFalse(lines.isEmpty(), String.valueOf(false));

                    }

                }
            }
        }
    }
        @Test
        void jsonFileParsingTest() throws IOException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File("src/test/resources/DNS.json"));
            Assertions.assertEquals("SKU-12345", jsonNode.get("items").get(0).get("productId").asText());
            System.out.println(jsonNode.get("items").get(0).get("productId").asText());
        }
    }




