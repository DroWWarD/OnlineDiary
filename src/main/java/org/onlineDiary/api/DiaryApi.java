package org.onlineDiary.api;

import lombok.RequiredArgsConstructor;
import org.onlineDiary.model.Student;
import org.onlineDiary.dto.SimpleResponse;
import org.onlineDiary.services.MainService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class MainController {
    private final MainService service;

    @GetMapping("/{class}")
    public List<Student> getStudentsByClass(@PathVariable(value = "class") String studClass, Model model){
        return service.getStudentsByClass(studClass);
    }

    @GetMapping("load")
    public SimpleResponse loadDataFromFile(Model model){
        return service.loadDataFromFile();
    }

}
