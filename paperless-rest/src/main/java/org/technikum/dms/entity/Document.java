package org.technikum.dms.entity;
import org.technikum.dms.dto.DocumentDTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="files")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    private String id;
    private String filename;
    private long filesize;
    private String filetype;
    private LocalDateTime uploadDate;
    private boolean ocrJobDone;

    @Lob
    private byte[] file;

    public Document(DocumentDTO dto) {
        this.id = dto.getId();
        this.filename = dto.getFilename();
        this.filesize = dto.getFilesize();
        this.filetype = dto.getFiletype();
        this.uploadDate = dto.getUploadDate();
        this.ocrJobDone = dto.isOcrJobDone();
        this.file = dto.getFileData();
    }
}