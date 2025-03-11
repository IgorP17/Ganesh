package org.example.demo.controller;

import org.example.demo.model.Request;
import org.example.demo.repository.RequestRepository;
import org.example.demo.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RequestController {
    private final RequestRepository requestRepository;
    private final KafkaProducerService kafkaProducerService;

    public RequestController(RequestRepository requestRepository, KafkaProducerService kafkaProducerService) {
        this.requestRepository = requestRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/send")
    public String sendRequest(@RequestBody String data) {
        Request request = new Request();
        request.setData(data);
        request = requestRepository.save(request);

        kafkaProducerService.sendMessage("requests", request.getId().toString(), data);
        return "Request saved with ID: " + request.getId();
    }
}