package org.onlineDiary.api;

import lombok.RequiredArgsConstructor;
import org.onlineDiary.dto.Response;
import org.onlineDiary.services.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class DiaryApi {
    private final MainService service;

    @PutMapping("/v1/new-plan")
    public ResponseEntity<String> loadDataFromFile(){
        return service.loadDataFromFile();
    }

    @GetMapping("/v1/students/{id}")
    public ResponseEntity<Response> getStudentById(@PathVariable(value = "id") int id){
        return service.getStudentById(id);
    }

    @GetMapping("/v1/groups/{group}/students/average-mark")
    public ResponseEntity<Response> getStudentsByClass(@PathVariable(value = "group") String squad,
                                       @RequestParam(value = "limit", defaultValue = "100") Integer limit,
                                       @RequestParam(value = "offsetPage", defaultValue = "0") Integer offset){
        return service.getStudentsWithAverageGradesByClass(squad, limit, offset);
    }

    @PatchMapping("/v1/students/{id}/marks/{subject}/{newMark}")
    public ResponseEntity<Response> setGradeForStudentIdAndSubject(@PathVariable(value = "id") int id,
                                                                 @PathVariable(value = "subject") String subject,
                                                                 @PathVariable(value = "newMark") int newGrade){
        return service.setNewGradeForStudent(id, subject, newGrade);
    }

    @PutMapping("/v1/groups/{group}/students/{family}/{name}/{age}")
    public ResponseEntity<Response> addNewStudent(@PathVariable(value = "group") String group,
                                                 @PathVariable(value = "family") String family,
                                                 @PathVariable(value = "name") String name,
                                                 @PathVariable(value = "age") int age){
        return service.addNewStudent(group, family, name, age);
    }
    @PutMapping("/v1/students/{id}/marks/{subject}/{newMark}")
    public ResponseEntity<Response> addNewGrade(@PathVariable(value = "id") int id,
                                              @PathVariable(value = "subject") String subject,
                                              @PathVariable(value = "newMark") int grade) {
        return service.addNewGrade(id, subject, grade);
    }

}
