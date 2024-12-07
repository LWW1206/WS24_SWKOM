package org.technikum.dms.document;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.technikum.dms.DmsApplication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = {DmsApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Slf4j
@TestPropertySource("/application-test.properties")
public class DocumentRepositoryTest {
    @Autowired
    DocumentRepository repo;

    private Document document;

    @BeforeEach
    void setUp() {
        document = new Document();
        document.setName("name");
        document.setContent("content");
        repo.save(document);
    }

    @Test
    void findAll(){
        List<Document> all = repo.findAll();
        assertEquals(all.size(), 1);
    }
}
