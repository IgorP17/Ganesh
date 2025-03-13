package demo.service;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
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
    public void processRequest(String key, String data) {
        ProcessedRequest processedRequest = new ProcessedRequest();
        processedRequest.setRequestId(Long.parseLong(key));
        processedRequest.setData(data);
        processedRequest.setStatus("SUCCESS");
        processedRequestRepository.save(processedRequest);

        kafkaProducerService.sendMessage("statuses", key, key + ":SUCCESS");
    }
}
