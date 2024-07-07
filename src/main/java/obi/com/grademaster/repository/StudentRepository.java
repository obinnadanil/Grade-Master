package obi.com.grademaster.repository;

import obi.com.grademaster.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> getStudentByRegNumber(String regNumber);
}

