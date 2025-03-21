package demo.service;

import demo.model.ProcessedRequest;
import demo.repository.ProcessedRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProcessedRequestService {

    @Autowired
    private ProcessedRequestRepository processedRequestRepository;

    // Ищем запрос по ID
    public String getRequestData(Long id) {
        Optional<ProcessedRequest> requestOptional = processedRequestRepository.findByRequestId(id);

        if (requestOptional.isPresent()) {
            ProcessedRequest request = requestOptional.get();
            return "Request ID: " + request.getId() + "\n" +
                    "Data: " + request.getData() + "\n" +
                    "Request id: " + request.getRequestId() + "\n" +
                    "Status: " + request.getStatus() + "\n" +
                    "Processed at: " + request.getProcessedAt();
        } else {
            return "Request not found";
        }
    }
}