package demo.controller;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/processed-requests")
public class ProcessedRequestController {

    @Autowired
    private ProcessedRequestRepository processedRequestRepository;

    @GetMapping("/by-request-id/{requestId}")
    public List<ProcessedRequest> getProcessedRequestsByRequestId(@PathVariable Long requestId) {
        return processedRequestRepository.findByRequestId(requestId);
    }
}