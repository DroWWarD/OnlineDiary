package org.onlineDiary.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineDiary.dto.*;
import org.onlineDiary.services.MainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class DiaryApi {
    private final MainService service;

    @PutMapping("/v1/new-plan")
    public Response loadDataFromFile(){
        int rowsToBase = service.loadDataFromFile();
        log.info(rowsToBase + " строк успешно загружены в базу");
        return new ResponseOK("В БД загружено " + rowsToBase + " строк из файла");
    }

    @GetMapping("/v1/students/{id}")
    public StudentWithGrades getStudentById(@PathVariable(value = "id") int id){
        return service.getStudentById(id);
    }

    @GetMapping("/v1/groups/{group}/students/average-grade")
    public Response getStudentsByClass(@PathVariable(value = "group") String squad,
                                         @RequestParam(value = "limit", defaultValue = "100") Integer limit,
                                         @RequestParam(value = "offsetPage", defaultValue = "0") Integer offset){
        List<StudentWithAverageGrade> result = service.getStudentsWithAverageGradesByClass(squad, limit, offset);
        return new ResponseOK("Получено результатов: " + result.size() + " из лимита " + limit +
                ". пропущено результатов согласно параметрам limit и offset: " + limit * offset, result);
    }

    @PatchMapping("/v1/students/grades")
    public Response editGrade(@RequestBody GradeDTO gradeDTO){
        List<StudentWithGrades> result = service.editGrade(gradeDTO.getId(), gradeDTO.getSubject(), gradeDTO.getGrade());
        return new ResponseOK("Оценка изменена. Новые данные/старые данные:", result);
    }

    @PutMapping("/v1/students/grades")
    public StudentWithGrades addNewGrade(@RequestBody GradeDTO gradeDTO) {
        return service.addNewGrade(gradeDTO.getId(), gradeDTO.getSubject(), gradeDTO.getGrade());
    }

    @PutMapping("/v1/groups/students")
    public StudentWithGrades addNewStudent(@RequestBody StudentDTO studentDTO){
        return service.addNewStudent(studentDTO.getGroup(), studentDTO.getFamily(), studentDTO.getName(), studentDTO.getAge());
    }
}
