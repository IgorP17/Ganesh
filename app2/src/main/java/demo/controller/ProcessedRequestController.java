package demo.controller;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import demo.service.ProcessedRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/processed-requests")
public class ProcessedRequestController {

    @Autowired
    private ProcessedRequestRepository processedRequestRepository;

    @GetMapping(value = "/by-request-id/{requestId}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public String getProcessedRequestByRequestId(@PathVariable Long requestId) {
        ProcessedRequest pr = processedRequestRepository.findFirstByRequestId(requestId);

        if (pr == null) {
            return "APP2: Запрос с ID" + requestId + "не найден";
        }

        return String.format(
                "ID: %d\nRequest ID: %d\nData: %s\nStatus: %s\nProcessed At: %s",
                pr.getId(),
                pr.getRequestId(),
                pr.getData(),
                pr.getStatus(),
                pr.getProcessedAt()
        );
    }
}