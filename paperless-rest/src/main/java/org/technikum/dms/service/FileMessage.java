package org.technikum.dms.service;

import java.io.Serializable;

public class FileMessage implements Serializable {
    private String fileName;
    private String contentType;
    private byte[] fileData;

    public FileMessage(String fileName, String contentType, byte[] fileData) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }
}
