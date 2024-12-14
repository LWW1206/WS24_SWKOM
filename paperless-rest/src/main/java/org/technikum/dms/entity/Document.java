package org.technikum.dms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "t_documents")
public class Document {
    public Document() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String status;

    @Lob
    private byte[] file;  // Store the file content as byte array

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();
}