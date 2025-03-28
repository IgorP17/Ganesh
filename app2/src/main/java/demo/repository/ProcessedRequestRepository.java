package demo.repository;

import demo.model.ProcessedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedRequestRepository extends JpaRepository<ProcessedRequest, Long> {
    // Было: List<ProcessedRequest> findByRequestId(Long requestId);
    ProcessedRequest findFirstByRequestId(Long requestId); // Возвращает первую найденную запись
}