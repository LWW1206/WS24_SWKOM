package org.technikum.paperless.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OcrService {

    private final Tesseract tesseract;

    public OcrService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/"); // Path to traineddata
        tesseract.setLanguage("eng"); // Set language
    }

    public String performOcr(File file) throws TesseractException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist or is null.");
        }

        try {
            return tesseract.doOCR(file);
        } catch (TesseractException e) {
            throw new TesseractException("Failed to process OCR for file: " + file.getAbsolutePath(), e);
        }
    }
}
