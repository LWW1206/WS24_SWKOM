package org.technikum.dms.document;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
    public void test1(){
        Document document = new Document();
        document.setId(0L);
        document.setContent("testContent");

        Set violations = validator.validate(document);
        assertEquals(violations, false);

        DocumentDTO documentDTO = documentMapper.toDTO(document);
        assertEquals(documentDTO.getContent(), "testContent");
    }
}