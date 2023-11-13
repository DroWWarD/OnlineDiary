package org.onlineDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StudentWithAverageGrade extends StudentResponse {
    private int id;
    private String family;
    private String name;
    private int age;
    private String group;
    private double average;
}
