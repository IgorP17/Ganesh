package org.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;
    private String status = "PENDING";
}
