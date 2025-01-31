package org.technikum.dms.entity;

import jakarta.validation.constraints.*;
import org.technikum.dms.dto.DocumentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @NotBlank(message = "Document ID cannot be blank")
    private String id;

    @NotBlank(message = "Filename cannot be blank")
    @Size(max = 255, message = "Filename cannot exceed 255 characters")
    private String filename;

    @Min(value = 0, message = "Filesize must be non-negative")
    private long filesize;

    @NotBlank(message = "Filetype cannot be blank")
    private String filetype;

    @PastOrPresent(message = "Upload date cannot be in the future")
    private LocalDateTime uploadDate;

    private boolean ocrJobDone;

    @Lob
    @NotNull(message = "File content cannot be null")
    private byte[] file;

    public Document(String documentId, String filename) {
        this.id = documentId;
        this.filename = filename;
    }

    public Document(DocumentDTO documentDTO) {
        this.id = documentDTO.getId();
        this.filename = documentDTO.getFilename();
        this.filesize = documentDTO.getFilesize();
        this.filetype = documentDTO.getFiletype();
        this.uploadDate = documentDTO.getUploadDate();
        this.ocrJobDone = documentDTO.isOcrJobDone();
    }
}