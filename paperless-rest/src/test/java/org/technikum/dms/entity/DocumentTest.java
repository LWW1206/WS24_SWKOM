package org.technikum.dms.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailWhenIdIsBlank() {
        Document document = new Document("", "example.pdf");
        document.setFilesize(1024);
        document.setFiletype("application/pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setOcrJobDone(false);
        document.setFile(new byte[]{1, 2, 3});

        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Document ID cannot be blank")));
    }

    @Test
    void shouldFailWhenFileSizeIsNegative() {
        Document document = new Document("123", "example.pdf");
        document.setFilesize(-10);
        document.setFiletype("application/pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setOcrJobDone(false);
        document.setFile(new byte[]{1, 2, 3});

        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Filesize must be non-negative")));
    }

    @Test
    void shouldFailWhenUploadDateIsFuture() {
        Document document = new Document("123", "example.pdf");
        document.setFilesize(1024);
        document.setFiletype("application/pdf");
        document.setUploadDate(LocalDateTime.now().plusDays(1)); // Future date
        document.setOcrJobDone(false);
        document.setFile(new byte[]{1, 2, 3});

        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Upload date cannot be in the future")));
    }

    @Test
    void shouldPassWhenValidDocument() {
        Document document = new Document("123", "example.pdf");
        document.setFilesize(1024);
        document.setFiletype("application/pdf");
        document.setUploadDate(LocalDateTime.now());
        document.setOcrJobDone(false);
        document.setFile(new byte[]{1, 2, 3});

        Set<ConstraintViolation<Document>> violations = validator.validate(document);

        assertTrue(violations.isEmpty());
    }
}
