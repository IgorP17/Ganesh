package demo.service;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {
    private final ProcessedRequestRepository processedRequestRepository;
    private final KafkaProducerService kafkaProducerService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    public KafkaConsumerService(ProcessedRequestRepository processedRequestRepository, KafkaProducerService kafkaProducerService) {
        this.processedRequestRepository = processedRequestRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "requests", groupId = "request-consumer")
    public void processRequest(ConsumerRecord<Long, String> record) {
        Long key = record.key(); // Получаем ключ
        String data = record.value(); // Получаем значение
        logger.info("Received key: {} (Type: {})", key, key.getClass().getSimpleName());
        logger.info("Received data: {} (Type: {})", data, data.getClass().getSimpleName());

        ProcessedRequest processedRequest = new ProcessedRequest();
        processedRequest.setRequestId(key);
        processedRequest.setData(data);
        processedRequest.setProcessedAt(LocalDateTime.now());
        processedRequest.setStatus("SUCCESS");
        try {
            processedRequestRepository.save(processedRequest);
            logger.info("Request saved to database: {}", processedRequest);
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage(), e);
        }

        kafkaProducerService.sendMessage("statuses", key, key + ":SUCCESS");
    }
}
