package org.technikum.paperless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.technikum.paperless.configs.PaperlessWorkerProperties;


/* Worker (step 4) consumes the OCR job from the processing queue. */
@SpringBootApplication
@EnableConfigurationProperties(PaperlessWorkerProperties.class)
public class PaperlessWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaperlessWorkerApplication.class, args);
    }
}