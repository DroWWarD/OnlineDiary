package org.onlineDiary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class StudentWithGrades extends StudentDTO{
    private int id;
    private String family;
    private String name;
    private int age;
    private String group;
    private Map<String, Integer> grades = new HashMap<>();

    public static StudentWithGrades copyOf(StudentWithGrades original) {
        StudentWithGrades copy = new StudentWithGrades();
        copy.id = original.id;
        copy.family = original.family;
        copy.name = original.name;
        copy.age = original.age;
        copy.group = original.group;
        copy.grades = Map.copyOf(original.grades);
        return copy;
    }
}
