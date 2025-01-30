package org.technikum.dms.entity;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class FileMessage implements Serializable {
    private String fileName;
    private String contentType;
    private byte[] fileData;

    public FileMessage(String fileName, String contentType, byte[] fileData) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileData = fileData;
    }
}
