package org.onlineDiary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineDiary.dto.*;
import org.onlineDiary.exceptions.IllegalArgsException;
import org.onlineDiary.exceptions.ResourceNotFoundException;
import org.onlineDiary.model.*;
import org.onlineDiary.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
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
    public int loadDataFromFile() {
        int count = 0;
        log.info("Запущен алгоритм загрузки данных в базу из файла. Подождите, " +
                "операция может занять несколько минут");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(uploadPath))) {
            String headLine = bufferedReader.readLine();
            String[] splittedHeadLine = headLine.split(";");
            Plan plan = addPlan(headLine.split(";"));
            while (bufferedReader.ready()) {
                if (count % 1000 == 0) log.info("Из файла прочитано " + count / 1000 + " тыс. строк");
                String row = bufferedReader.readLine();
                String[] splittedRow = row.split(";");
                Squad squad = addSquad(splittedRow[3], plan);
                int age = Integer.parseInt(splittedRow[2]);
                Student student = addStudent(splittedRow[0], splittedRow[1], age, squad);
                addGrades(splittedRow, plan, splittedHeadLine, student);
                count++;
            }
            planRepository.save(plan);
            subjectRepository.saveAll(plan.getSubjects());
            log.info("Сущности сформированы, выполняется вставка элементов в БД");
        } catch (IOException e) {
            log.error("Ошибка чтения файла");
            return 0;
        }
        return count;
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

    private Student addStudent(String family, String name, int age, Squad squad) {
        checkName(family);
        checkName(name);
        checkAge(age);
        Student student = new Student();
        student.setFamily(family);
        student.setName(name);
        student.setAge(age);
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
    public List<StudentWithAverageGrade> getStudentsWithAverageGradesByClass(String squad, Integer limit, Integer offset) {
        checkSquadName(squad);
        return studentRepository.searchBySquad(squad, PageRequest.of(offset, limit));

    }

    @Transactional
    public List<StudentWithGrades> editGrade(int studentId, String subject, int newGrade) {
        checkGrade(newGrade);
        checkName(subject);
        List<StudentWithGrades> resultList = new ArrayList<>();
        Grade grade = gradeRepository.findByStudentIdAndSubjectName(studentId, subject).orElseThrow(() ->
                new ResourceNotFoundException("Студент с id " + studentId + " не найден в БД"));
        Student student = grade.getStudent();
        StudentWithGrades studentOldData = createStudentWithGradesResponse(student);
        StudentWithGrades studentNewData = StudentWithGrades.copyOf(studentOldData);
        grade.setGrade(newGrade);
        gradeRepository.save(grade);
        studentOldData.getGrades().replace(grade.getSubject().getName(), newGrade);
        resultList.add(studentOldData);
        resultList.add(studentNewData);
        return resultList;
    }

    private StudentWithGrades createStudentWithGradesResponse(Student student) {
        StudentWithGrades result = new StudentWithGrades();
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
    public StudentWithGrades addNewGrade(int id, String subjectName, int newGrade) {
        checkGrade(newGrade);
        checkName(subjectName);
        Grade grade = new Grade();
        grade.setGrade(newGrade);
        Subject subject = subjectRepository.findByName(subjectName).orElseThrow(() -> new ResourceNotFoundException("Предмет с именем " + subjectName + " не найден в БД"));
        grade.setSubject(subject);
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Студент с id = " + id + " не найден в БД"));
        grade.setStudent(student);
        gradeRepository.save(grade);
        student.getGrades().add(grade);
        return createStudentWithGradesResponse(student);
    }

    @Transactional
    public StudentWithGrades addNewStudent(String squadName, String family, String name, int age) {
        checkSquadName(squadName);
        checkName(family);
        checkName(name);
        checkAge(age);
        Student student = new Student();
        student.setFamily(family);
        student.setName(name);
        student.setAge(age);
        Squad squad = squadRepository.findByName(squadName).orElseThrow(() -> new ResourceNotFoundException("Группа с именем " + squadName + " не найдена в БД"));
        student.setSquad(squad);
        squad.getStudents().add(student);
        studentRepository.save(student);
        return createStudentWithGradesResponse(student);
    }

    private void checkSquadName(String name){
        if (!name.matches("([A-z]|[А-я]|[0-9])*")) {
            throw new IllegalArgsException("Недопустимое значение: " + name);
        }
    }
    private void checkAge(int age) {
        if (age < 1 || age > 100) {
            throw new IllegalArgsException("Недопустимое значение возраста: " + age);
        }
    }


    private void checkGrade(int grade) {
        if (grade < 1 || grade > 5) {
            throw new IllegalArgsException("Недопустимое значение оценки: " + grade);
        }
    }

    private void checkName(String name) {
        if (!name.matches("([A-z]|[А-я]|[Ё-ё])*")) {
            throw new IllegalArgsException("Недопустимое значение: " + name);
        }
    }

    @Transactional
    public StudentWithGrades getStudentById(int id) {
        Optional<Student> studentOptional = studentRepository.findById(id);
        return studentOptional.map(this::createStudentWithGradesResponse).orElse(null);
    }

}
