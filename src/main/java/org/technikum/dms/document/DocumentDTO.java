package org.technikum.dms.document;

import lombok.Data;

@Data
public class DocumentDTO {
    private String name;

    private String content;

    private String status;
}