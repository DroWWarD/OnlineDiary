package org.onlineDiary.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "text", nullable = false)
    private String name;
}
