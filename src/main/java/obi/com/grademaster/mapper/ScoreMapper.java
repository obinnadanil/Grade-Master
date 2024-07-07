package obi.com.grademaster.mapper;

import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.service.CourseService;
import obi.com.grademaster.service.StudentService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CourseService.class, StudentService.class}, injectionStrategy = InjectionStrategy.FIELD)
public abstract class ScoreMapper {

    @Autowired
    CourseService courseService;
    @Autowired
    StudentService studentService;



    @Mapping(target = "course", expression = "java(courseService.getCourseByCourseCode(scoreDto.getCourseCode()).orElseThrow(() -> new obi.com.grademaster.exception.CourseNotFoundException(\"courseNotFound\")))")
    @Mapping(target = "student", expression = "java(studentService.getStudentByRegNumber(scoreDto.getRegNumber()).orElseThrow(() -> new obi.com.grademaster.exception.StudentNotFoundException(\"studentNotFound\")))")
    public  abstract Score mapToScore(ScoreDto scoreDto);
    @Mapping(target = "grade", ignore = true)
    @Mapping(target = "courseCode", source = "course.courseCode")
    @Mapping(target = "regNumber", source = "student.regNumber")
    @Mapping(target = "courseName", source = "course.courseName")
    @Mapping(target = "id", source = "id")
    public abstract ScoreDto mapToScoreDto(Score score);

}
