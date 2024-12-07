package org.technikum.dms.mapper;

import org.mapstruct.Mapper;
import org.technikum.dms.entity.Document;
import org.technikum.dms.entity.DocumentDTO;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document toEntity(DocumentDTO dto);
    DocumentDTO toDTO(Document entity);
}