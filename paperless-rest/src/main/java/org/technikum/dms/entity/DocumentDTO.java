package org.technikum.dms.entity;

import lombok.Data;

@Data
public class DocumentDTO {
    public DocumentDTO(){

    }

    private String name;

    private String content;

    private String status;
}