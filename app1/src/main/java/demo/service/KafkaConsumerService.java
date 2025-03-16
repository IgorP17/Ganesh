package demo.service;

import demo.model.Request;
import demo.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final RequestRepository requestRepository;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    public KafkaConsumerService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @KafkaListener(topics = "statuses", groupId = "status-updater")
    public void updateStatus(String status) {
        logger.info("Received status: {}", status);
        String[] parts = status.split(":");
        Long id = Long.parseLong(parts[0]);
        String newStatus = parts[1];

        requestRepository.findById(id).ifPresent(request -> {
            request.setStatus(newStatus);
            requestRepository.save(request);
        });
    }
}