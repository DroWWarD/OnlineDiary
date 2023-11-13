package org.onlineDiary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentResponse {
    private int id;
    private String family;
    private String name;
    private int age;
}
