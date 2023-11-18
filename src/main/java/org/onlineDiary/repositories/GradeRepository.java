package org.onlineDiary.repositories;

import org.onlineDiary.model.Grade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GradeRepository extends CrudRepository<Grade, Long> {

    Optional<Grade> findByStudentIdAndSubjectName(int studentId, String subjectName);
}
