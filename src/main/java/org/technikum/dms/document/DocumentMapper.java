package org.technikum.dms.document;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    Document toEntity(DocumentDTO dto);
    DocumentDTO toDTO(Document entity);
}