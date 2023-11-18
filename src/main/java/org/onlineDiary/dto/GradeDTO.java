package org.onlineDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GradeDTO {
    private int id;
    private String subject;
    private int grade;
}
