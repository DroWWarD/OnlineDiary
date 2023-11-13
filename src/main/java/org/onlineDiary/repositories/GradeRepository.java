package org.onlineDiary.repositories;

import org.onlineDiary.model.Grade;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GradeRepository extends CrudRepository<Grade, Long> {

    Grade findByStudentIdAndSubjectName(int studentId, String subjectName);
}
