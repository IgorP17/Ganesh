package demo.controller;

import demo.model.Request;
import demo.repository.RequestRepository;
import demo.service.KafkaConsumerService;
import demo.service.KafkaProducerService;
import demo.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final RequestRepository requestRepository;
    private final KafkaProducerService kafkaProducerService;
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public RequestController(RequestRepository requestRepository, KafkaProducerService kafkaProducerService) {
        this.requestRepository = requestRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Autowired
    private RequestService requestService;

    @PostMapping("/send")
    public String sendRequest(@RequestBody String data) {
        logger.info("Got request: {}", data);
        Request request = new Request();
        request.setData(data);
        request = requestRepository.save(request);

        kafkaProducerService.sendMessage("requests", request.getId(), data);
        return "Request saved with ID: " + request.getId();
    }

    // Эндпоинт для поиска запроса по ID
    @GetMapping("/request/{id}")
    public String getRequestData(@PathVariable Long id) {
        return requestService.getRequestData(id);
    }
}