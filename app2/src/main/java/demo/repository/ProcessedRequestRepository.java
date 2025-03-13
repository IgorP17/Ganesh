package demo.repository;

import demo.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, Long> {
}