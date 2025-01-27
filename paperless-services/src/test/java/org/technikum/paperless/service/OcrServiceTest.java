package org.technikum.paperless.service;

import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OcrServiceTest {

    //@Test
    void performOcr_ShouldReturnText() throws TesseractException {
        OcrService ocrService = new OcrService();
        File testFile = new File("src/test/resources/sample.pdf");
        String result = ocrService.performOcr(testFile);
        assertNotNull(result, "OCR result should not be null");
    }
}
