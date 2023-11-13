package org.onlineDiary.services;

import lombok.RequiredArgsConstructor;
import org.onlineDiary.dto.*;
import org.onlineDiary.model.*;
import org.onlineDiary.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MainService {
    private final GradeRepository gradeRepository;
    private final PlanRepository planRepository;
    private final SquadRepository squadRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    @Value("${upload.path}")
    private String uploadPath;

    @Transactional
    public ResponseEntity<String> loadDataFromFile() {
        try{
            System.out.println("Запущен алгоритм загрузки данных в базу из файла. Подождите, " +
                    "операция может занять несколько минут");
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(uploadPath))) {
                String headLine = bufferedReader.readLine();
                String[] splittedHeadLine = headLine.split(";");
                Plan plan = addPlan(headLine.split(";"));
                int count = 0;
                while (bufferedReader.ready()) {
                    if (count % 1000 == 0) System.out.println("Из файла прочитано " + count / 1000 + " тыс. строк");
                    String row = bufferedReader.readLine();
                    String[] splittedRow = row.split(";");
                    Squad squad = addSquad(splittedRow[3], plan);
                    Student student = addStudent(splittedRow[0], splittedRow[1], splittedRow[2], squad);
                    addGrades(splittedRow, plan, splittedHeadLine, student);
                    count++;
                }
                planRepository.save(plan);
                subjectRepository.saveAll(plan.getSubjects());
                System.out.println("Сущности сформированы, выполняется вставка элементов в БД");
            } catch (IOException e) {
                return new ResponseEntity<>("Ошибка чтения файла", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("Данные успешно загружены в базу", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Произошла ошибка", HttpStatus.BAD_REQUEST);
        }
    }

    private void addGrades(String[] splittedRow, Plan plan, String[] splittedHeadLine, Student student) {
        for (int i = 4; i < splittedRow.length; i++) {
            Grade grade = new Grade();
            for (Subject subject : plan.getSubjects()) {
                if (subject.getName().equals(splittedHeadLine[i])) {
                    grade.setGrade(Integer.parseInt(splittedRow[i]));
                    grade.setStudent(student);
                    grade.setSubject(subject);
                    subject.getGrades().add(grade);
                    student.getGrades().add(grade);
                }
            }
        }
    }

    private Student addStudent(String family, String name, String age, Squad squad) {
        Student student = new Student();
        student.setFamily(family);
        student.setName(name);
        if (age.matches("^([1-9]|[1-9][0-9])$")) {
            student.setAge(Integer.parseInt(age));
        } else {
            throw new IllegalArgumentException("В файле содержатся некорректные данные в поле age");
        }
        student.setSquad(squad);
        squad.getStudents().add(student);
        return student;
    }

    private Squad addSquad(String name, Plan plan) {
        Squad squad = null;
        for (Squad s : plan.getSquads()) {
            if (s.getName().equals(name)) {
                squad = s;
            }
        }
        if (squad == null) {
            squad = new Squad();
            squad.setName(name);
            squad.setPlan(plan);
        }
        plan.getSquads().add(squad);
        return squad;
    }

    private Plan addPlan(String[] headLine) {
        Plan plan = new Plan();
        StringBuilder planName = new StringBuilder();
        for (int i = 4; i < headLine.length; i++) {
            planName.append(headLine[i]);
            Subject subject = new Subject();
            subject.setName(headLine[i]);
            subject.getPlans().add(plan);
            plan.getSubjects().add(subject);
        }
        plan.setName(planName.toString());
        return plan;
    }

    @Transactional
    public ResponseEntity<Response> getStudentsWithAverageGradesByClass(String squad, Integer limit, Integer offset) {
        try {
            List<StudentWithAverageGrade> studentList = studentRepository.searchBySquad(squad, PageRequest.of(offset, limit));
            int listSize = studentList.size();
            return new ResponseEntity<>(new Response(200, "OK", listSize, studentList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(404, "Произошла ошибка", 0, null), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<Response> setNewGradeForStudent(int studentId, String subject, int newGrade) {
        try {
            List<StudentResponse> responseList = new ArrayList<>();
            Grade grade = gradeRepository.findByStudentIdAndSubjectName(studentId, subject);
            Student student = grade.getStudent();
            StudentWithGradesResponse studentOldData = createStudentWithGradesResponse(student);
            responseList.add(studentOldData);
            StudentWithGradesResponse studentNewData = StudentWithGradesResponse.copyOf(studentOldData);
            grade.setGrade(newGrade);
            gradeRepository.save(grade);
            studentOldData.getGrades().replace(grade.getSubject().getName(), newGrade);
            responseList.add(studentNewData);
            return new ResponseEntity<>(new Response(200, "Оценка изменена. Новые данные/старые данные прилагаются:",
                    responseList.size(), responseList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(404, "Произошла ошибка", 0, null),
                    HttpStatus.NOT_FOUND);
        }
    }

    private StudentWithGradesResponse createStudentWithGradesResponse(Student student) {
        StudentWithGradesResponse result = new StudentWithGradesResponse();
        result.setId(student.getId());
        result.setFamily(student.getFamily());
        result.setName(student.getName());
        result.setAge(student.getAge());
        result.setGroup(student.getSquad().getName());
        for (Grade grade : student.getGrades()) {
            result.getGrades().put(grade.getSubject().getName(), grade.getGrade());
        }
        return result;
    }

    @Transactional
    public ResponseEntity<Response> addNewGrade(int id, String subjectName, int mark) {
        try {
            Grade grade = new Grade();
            grade.setGrade(mark);
            Subject subject = subjectRepository.findByName(subjectName).get();
            grade.setSubject(subject);
            Student student = studentRepository.findById(id).get();
            grade.setStudent(student);
            gradeRepository.save(grade);
            student.getGrades().add(grade);
            List<StudentResponse> responseList = new ArrayList<>();
            StudentWithGradesResponse response = createStudentWithGradesResponse(student);
            responseList.add(response);
            return new ResponseEntity<>(new Response(200, "Оценка добавлена:",
                    responseList.size(), responseList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(404, "Произошла ошибка", 0, null),
                    HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public ResponseEntity<Response> addNewStudent(String squadName, String family, String name, int age) {
        try {
            List<StudentResponse> responseList = new ArrayList<>();
            Student student = new Student();
            student.setFamily(family);
            student.setName(name);
            student.setAge(age);
            Squad squad = squadRepository.findByName(squadName).get();
            student.setSquad(squad);
            squad.getStudents().add(student);
            studentRepository.save(student);
            responseList.add(createStudentWithGradesResponse(student));
            return new ResponseEntity<>(new Response(200, "В базу добавлен новый ученик:",
                    responseList.size(), responseList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(404, "Произошла ошибка", 0, null),
                    HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public ResponseEntity<Response> getStudentById(int id) {
        try {
            Student student = studentRepository.findById(id).get();
            StudentWithGradesResponse response = createStudentWithGradesResponse(student);
            List<StudentResponse> responseList = new ArrayList<>();
            responseList.add(response);
            return new ResponseEntity<>(new Response(200, "OK", 1, responseList), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response(404, "Произошла ошибка", 0, null),
                    HttpStatus.NOT_FOUND);
        }
    }

}
