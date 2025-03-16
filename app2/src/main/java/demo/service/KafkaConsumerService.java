package demo.service;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {
    private final ProcessedRequestRepository processedRequestRepository;
    private final KafkaProducerService kafkaProducerService;

    public KafkaConsumerService(ProcessedRequestRepository processedRequestRepository, KafkaProducerService kafkaProducerService) {
        this.processedRequestRepository = processedRequestRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @KafkaListener(topics = "requests", groupId = "request-consumer")
    public void processRequest(ConsumerRecord<Long, String> record) {
        Long key = record.key(); // Получаем ключ
        String data = record.value(); // Получаем значение
        System.out.println("Received key: " + key + " (Type: " + key.getClass().getSimpleName() + ")");
        System.out.println("Received data: " + data + " (Type: " + data.getClass().getSimpleName() + ")");

        ProcessedRequest processedRequest = new ProcessedRequest();
        processedRequest.setRequestId(key);
        processedRequest.setData(data);
        processedRequest.setProcessedAt(LocalDateTime.now());
        processedRequest.setStatus("SUCCESS");
        try {
            processedRequestRepository.save(processedRequest);
            System.out.println("Request saved to database: " + processedRequest);
        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
        }

        kafkaProducerService.sendMessage("statuses", key, key + ":SUCCESS");
    }
}
