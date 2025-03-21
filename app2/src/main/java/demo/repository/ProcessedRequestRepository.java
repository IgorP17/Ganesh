package demo.repository;

import demo.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, Long> {
    List<ProcessedRequest> findByRequestId(Long requestId);
}