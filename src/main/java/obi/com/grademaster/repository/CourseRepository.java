package obi.com.grademaster.repository;

import obi.com.grademaster.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> getCourseByCourseCode(String regNumber);

}
