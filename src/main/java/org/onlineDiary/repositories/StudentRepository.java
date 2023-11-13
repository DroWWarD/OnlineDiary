package org.onlineDiary.repositories;

import org.onlineDiary.dto.StudentWithAverageGrade;
import org.onlineDiary.model.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends CrudRepository<Student, Long> {

    @Query("""
        SELECT new org.onlineDiary.dto.StudentWithAverageGrade(s.id, s.family, s.name, s.age, :squadName, ROUND (AVG(g.grade), 2))
        FROM Student s
        JOIN Squad sq ON sq.id=s.squad.id
        JOIN Grade g ON g.student.id=s.id
        WHERE sq.name LIKE :squadName
        GROUP BY s.id
    """)
    List<StudentWithAverageGrade> searchBySquad(@Param("squadName") String squadName, Pageable pageable);

    Optional<Student> findById(int id);

}
