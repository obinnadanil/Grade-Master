package obi.com.grademaster.service;
import obi.com.grademaster.entity.Course;
import obi.com.grademaster.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    CourseRepository courseRepository;

    public Optional<Course> getCourseById(Long id){
        return courseRepository.findById(id);
    }

    public Optional<Course> getCourseByCourseCode(String regNumber){
       return courseRepository.getCourseByCourseCode(regNumber);
    }
    public List<Course> getCourses(){
        return courseRepository.findAll();
    }
    public void saveCourse(Course course){
        courseRepository.save(course);

    }
    public void deleteCourse(Long id){
       courseRepository.deleteById(id);
    }

    public boolean isCourseExist(String courseCode) {
        return courseRepository
                .findAll()
                .stream()
                .anyMatch(course -> course.getCourseCode().equals(courseCode));
    }


//    public List<Score> getCourseScoresById(Long id, List<Score> scores) {
//
//        return scores.stream().filter(score -> score.getCourse().getId().equals(id)).toList();
//
//    }
}
