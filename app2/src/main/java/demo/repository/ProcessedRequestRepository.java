package demo.repository;

import demo.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, Long> {
    Optional<ProcessedRequest> findByRequestId(Long requestId);
}