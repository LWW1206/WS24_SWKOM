package org.technikum.dms.service;
import org.technikum.dms.config.PaperlessRestProperties;
import org.technikum.dms.dto.DocumentDTO;
import org.technikum.dms.entity.Document;
import org.technikum.dms.rabbitmq.IOcrJobProducer;
import org.technikum.dms.repository.DocumentRepository;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.minio.*;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements IDocumentService {
    private final DocumentRepository documentRepository;
    private final IOcrJobProducer ocrJobProducer;
    private final MinioClient minio;
    private final ElasticsearchClient esClient;
    private final String bucketName = "documents";

    public DocumentServiceImpl(
            DocumentRepository documentRepository,
            IOcrJobProducer ocrJobProducer,
            PaperlessRestProperties props
    ) {
        this.documentRepository = documentRepository;
        this.ocrJobProducer = ocrJobProducer;
        var m = props.minio();
        this.minio = MinioClient.builder()
                .endpoint(m.url())
                .credentials(m.accessKey(), m.secretKey())
                .build();
        var esUri = props.elasticsearch().uris();
        var restClient = RestClient.builder(HttpHost.create(esUri)).build();
        var transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.esClient = new ElasticsearchClient(transport);
    }

    @Override
    public DocumentDTO uploadFile(MultipartFile file) throws Exception {
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        var id = UUID.randomUUID().toString();
        var docDto = DocumentDTO.builder()
                .id(id)
                .filename(file.getOriginalFilename())
                .filesize(file.getSize())
                .filetype(file.getContentType())
                .uploadDate(LocalDateTime.now())
                .ocrJobDone(false)
                .build();
        documentRepository.save(new Document(docDto));
        if (!minio.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minio.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
        minio.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(id)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        ocrJobProducer.triggerOcrJob(id, docDto.getFilename());
        return docDto;
    }

    @Override
    public void deleteDocument(String id) throws Exception {
        if (!documentRepository.existsById(id)) {
            throw new IllegalArgumentException("Document not found: " + id);
        }
        documentRepository.deleteById(id);
        minio.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(id).build());
    }

    @Override
    public byte[] getDocumentFileById(String id) throws Exception {
        try (InputStream s = minio.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(id).build()
        )) {
            return s.readAllBytes();
        }
    }

    @Override
    public DocumentDTO getDocumentById(String id) {
        return documentRepository.findById(id)
                .map(d -> DocumentDTO.builder()
                        .id(d.getId())
                        .filename(d.getFilename())
                        .filesize(d.getFilesize())
                        .filetype(d.getFiletype())
                        .uploadDate(d.getUploadDate())
                        .ocrJobDone(d.isOcrJobDone())
                        .fileData(d.getFile())
                        .build()
                )
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(d -> DocumentDTO.builder()
                        .id(d.getId())
                        .filename(d.getFilename())
                        .filesize(d.getFilesize())
                        .filetype(d.getFiletype())
                        .uploadDate(d.getUploadDate())
                        .ocrJobDone(d.isOcrJobDone())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentDTO> searchDocuments(String query) {
        try {
            var req = SearchRequest.of(s -> s
                    .index("documents")
                    .query(q -> q.bool(b -> b
                            .should(m -> m.match(mb -> mb.field("ocrText").query(query)))
                            .should(m -> m.wildcard(wb -> wb.field("filename").value("*" + query + "*")))
                            .should(m -> m.match(mb -> mb.field("documentId").query(query)))
                    ))
            );
            var resp = esClient.search(req, Map.class);
            var hits = resp.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .toList();
            var results = new ArrayList<DocumentDTO>();
            for (var docMap : hits) {
                var docId = (String) docMap.get("documentId");
                if (docId != null) {
                    documentRepository.findById(docId).ifPresent(ent -> results.add(DocumentDTO.builder()
                            .id(ent.getId())
                            .filename(ent.getFilename())
                            .filesize(ent.getFilesize())
                            .filetype(ent.getFiletype())
                            .uploadDate(ent.getUploadDate())
                            .ocrJobDone(ent.isOcrJobDone())
                            .build()));
                }
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to search docs", e);
        }
    }
}
