package org.onlineDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class StudentResponse {
    private int id;
    private String family;
    private String name;
    private int age;
    private String group;
    private Map<String, Integer> grades = new HashMap<>();
}
