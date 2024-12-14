package org.technikum.dms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.technikum.dms.entity.Document;
import org.technikum.dms.entity.DocumentDTO;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(source = "content", target = "file")
    Document toEntity(DocumentDTO dto);

    @Mapping(source = "file", target = "content")
    DocumentDTO toDTO(Document entity);

}
