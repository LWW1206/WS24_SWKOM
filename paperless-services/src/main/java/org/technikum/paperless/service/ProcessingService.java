package org.technikum.paperless.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import net.sourceforge.tess4j.Tesseract;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.technikum.paperless.configs.PaperlessWorkerProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.HashMap;

/* Worker pulls the PDF from MinIO (step 5), runs Tesseract.
   Worker indexes the doc in Elasticsearch (step 6b). */
@Service
public class ProcessingService {
    private final OcrJobProducerImpl resultSender;
    private final Tesseract tesseract;
    private final MinioClient minioClient;
    private final ElasticsearchClient esClient;

    public ProcessingService(
            OcrJobProducerImpl resultSender,
            PaperlessWorkerProperties props
    ) {
        this.resultSender = resultSender;
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        this.tesseract.setLanguage("eng");
        var m = props.minio();
        this.minioClient = MinioClient.builder()
                .endpoint(m.url())
                .credentials(m.accessKey(),m.secretKey())
                .build();
        var esUri = props.elasticsearch().uris();
        var restClient = RestClient.builder(HttpHost.create(esUri)).build();
        var transport = new RestClientTransport(restClient,new JacksonJsonpMapper());
        this.esClient = new ElasticsearchClient(transport);
    }

    @RabbitListener(queues = "${paperlessworker.rabbitmq.processing-queue}")
    public void processOcrJob(String rawMessage) {
        var json=new JSONObject(rawMessage);
        var docId=json.getString("documentId");
        var filename=json.optString("filename","unknown.pdf");
        try {
            var stream=minioClient.getObject(GetObjectArgs.builder()
                    .bucket("documents").object(docId).build());
            var tempFile=new File("/tmp/"+docId+".pdf");
            try(var fos=new FileOutputStream(tempFile)) {
                byte[] buffer=new byte[1024];int bytesRead;
                while((bytesRead=stream.read(buffer))!=-1) {
                    fos.write(buffer,0,bytesRead);
                }
            }
            var ocrText=tesseract.doOCR(tempFile);
            indexDocument(docId,filename,ocrText);
            resultSender.sendToResultQueue(docId,ocrText);
        } catch(Exception e) {}
    }

    private void indexDocument(String docId,String filename,String ocrText) throws Exception {
        var jsonMap=new HashMap<String,Object>();
        jsonMap.put("documentId",docId);
        jsonMap.put("filename",filename);
        jsonMap.put("ocrText",ocrText);
        jsonMap.put("@timestamp", Instant.now().toString());
        var req=IndexRequest.of(i->i.index("documents").id(docId).document(jsonMap));
        IndexResponse r=esClient.index(req);
    }
}