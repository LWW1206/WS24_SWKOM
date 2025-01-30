package org.technikum.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.technikum.dms.config.PaperlessRestProperties;

@SpringBootApplication
@EnableConfigurationProperties(PaperlessRestProperties.class)

public class DmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DmsApplication.class, args);
    }

}
