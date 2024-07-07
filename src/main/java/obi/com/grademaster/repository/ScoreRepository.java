package obi.com.grademaster.repository;

import obi.com.grademaster.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    @Query("SELECT COUNT(s) > 0 FROM Score s WHERE s.course.courseCode = :courseCode AND s.student.regNumber = :regNumber")
    boolean existsByCourseCodeAndRegNumber(String courseCode, String regNumber);

}
