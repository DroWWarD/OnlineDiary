package org.onlineDiary.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "plan")
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Column(name = "name", columnDefinition = "text", nullable = false)
    private String name;
    @ManyToMany
    @JoinTable(name="subject_plan",
            joinColumns=  @JoinColumn(name="plan_id", referencedColumnName="id"),
            inverseJoinColumns= @JoinColumn(name="subject_id", referencedColumnName="id"))
    private List<Subject> subjects = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "plan")
    private List<Squad> squads = new ArrayList<>();
}
