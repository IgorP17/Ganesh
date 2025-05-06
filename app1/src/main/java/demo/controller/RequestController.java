package demo.controller;

import demo.model.Request;
import demo.repository.RequestRepository;
import demo.service.KafkaProducerService;
import demo.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "APP1: Отправить запрос")
    public ResponseEntity<String> sendRequest(@RequestBody(required = false) String data) {
        if (null == data || data.isEmpty()) {
            logger.warn("REJECT request because length is zero");
            return ResponseEntity.badRequest().body("Invalid request: Empty message.");
        }

        if (data.length() > 1000) {
            logger.warn("REJECT request because length over 1000");
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Invalid request: Message too long.");
        }

        logger.info("Got request: {}", data);

        Request request = new Request();
        request.setData(data);
        request = requestRepository.save(request);

        kafkaProducerService.sendMessage("requests", request.getId(), data);
        return ResponseEntity.ok("Request saved with ID: " + request.getId());
    }

    // Эндпоинт для поиска запроса по ID
    @GetMapping("/request/{id}")
    public String getRequestData(@PathVariable Long id) {
        return requestService.getRequestData(id);
    }
}