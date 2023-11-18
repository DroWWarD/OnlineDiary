package org.onlineDiary.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseOK extends Response{
    private String message;
    private List<? extends StudentDTO> resultList = new ArrayList<>();

    public ResponseOK(String message) {
        this.message = message;
    }

    public ResponseOK(String message, List<? extends StudentDTO> resultList) {
        this.message = message;
        this.resultList = resultList;
    }
}
