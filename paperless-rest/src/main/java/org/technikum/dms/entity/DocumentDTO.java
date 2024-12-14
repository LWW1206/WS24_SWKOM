package org.technikum.dms.entity;

import lombok.Data;

@Data
public class DocumentDTO {

    private String name;

    private byte[] content;

    private String status;

    private Long id;

    // You can also add a method to convert content to a byte array if needed
}
