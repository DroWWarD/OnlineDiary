package org.onlineDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Response{
    private int code;
    private String message;
    private int resultsCount;
    private List<? extends StudentResponse> resultList = new ArrayList<>();
}
