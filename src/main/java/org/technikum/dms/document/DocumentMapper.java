package org.technikum.dms.document;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document toEntity(DocumentDTO dto);
    DocumentDTO toDTO(Document entity);
}