package guru.qa;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FilesTest {
    private ClassLoader cl = FilesTest.class.getClassLoader();

    @Test
    void unZipFileAndReadAll() throws Exception {
        try (InputStream is = cl.getResourceAsStream("fill.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }
        }
    }
}





