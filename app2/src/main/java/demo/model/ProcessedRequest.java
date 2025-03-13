package demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProcessedRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long requestId;
    private String data;
    private String status;
}