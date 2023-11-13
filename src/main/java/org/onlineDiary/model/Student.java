package org.onlineDiary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Column(name = "family", columnDefinition = "text", nullable = false)
    private String family;
    @Column(name = "name", columnDefinition = "text", nullable = false)
    private String name;
    @Column(name = "age", columnDefinition = "INT", nullable = false)
    private int age;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "squad_id")
    @Fetch(FetchMode.JOIN)
    private Squad squad;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "student")
    private List<Grade> grades = new ArrayList<>();
}
