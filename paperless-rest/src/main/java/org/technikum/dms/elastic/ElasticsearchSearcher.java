package org.technikum.dms.elastic;

import org.technikum.dms.dto.DocumentSearchResultDTO;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ElasticsearchSearcher {

    @Autowired
    ElasticsearchClient elasticsearchClient;

    public List<DocumentSearchResultDTO> searchDocuments(String query) {
        try {
            // Elasticsearch-Suchanfrage erstellen, die nach ocrText, filename und documentId sucht
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("documents")
                    .query(q -> q
                            .bool(b -> b
                                    .should(s1 -> s1.match(m -> m.field("ocrText").query(query)))  // Suche im OCR-Text
                                    .should(s2 -> s2.wildcard(w -> w.field("filename").value("*" + query + "*"))) // Wildcard-Suche für Teilstring im filename
                                    .should(s3 -> s3.match(m -> m.field("documentId").query(query))) // Suche in der Dokument-ID
                            )
                    )
            );

            SearchResponse<DocumentSearchResultDTO> searchResponse = elasticsearchClient.search(searchRequest, DocumentSearchResultDTO.class);

            log.info("Elasticsearch returned {} results for query: {}", searchResponse.hits().hits().size(), query);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching documents in Elasticsearch: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to search documents", e);
        }
    }
}