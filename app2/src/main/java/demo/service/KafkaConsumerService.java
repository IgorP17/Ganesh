package demo.service;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
        processedRequest.setStatus("SUCCESS");
        processedRequestRepository.save(processedRequest);

        kafkaProducerService.sendMessage("statuses", key, key + ":SUCCESS");
    }
}
