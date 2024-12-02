package org.technikum.dms.document;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@TestPropertySource("/application-test.properties")
public class DocumentMapperIntegrationTest {
    @Autowired
    private DocumentMapper documentMapper;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void documentCreatedAndMappedToDTO(){
        Document document = new Document();
        document.setId(0L);
        document.setName("test");
        document.setContent("testContent");

        DocumentDTO documentDTO = documentMapper.toDTO(document);
        assertEquals(documentDTO.getContent(), "testContent");
    }

    @Test
    public void validDocument(){
        Document document = new Document();
        document.setId(0L);
        document.setName("test");
        document.setContent("testContent");

        Set violations = validator.validate(document);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void invalidDocument(){
        Document document = new Document();
        document.setId(0L);
        document.setContent("testContent");

        Set violations = validator.validate(document);
        assertFalse(violations.isEmpty());
    }
}