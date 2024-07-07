package obi.com.grademaster.controller;

import jakarta.validation.Valid;
import obi.com.grademaster.DTO.CourseDto;
import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.DTO.ScoreStat;
import obi.com.grademaster.DTO.StudentDto;
import obi.com.grademaster.entity.Course;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.exception.CourseNotFoundException;
import obi.com.grademaster.exception.ScoreNotFoundException;
import obi.com.grademaster.mapper.CourseMapper;
import obi.com.grademaster.mapper.ScoreMapper;
import obi.com.grademaster.mapper.StudentMapper;
import obi.com.grademaster.service.CourseService;
import obi.com.grademaster.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@EnableMethodSecurity
public class CourseController {
    @Autowired
    CourseService courseService;
    @Autowired
    CourseMapper courseMapper;
    @Autowired
    ScoreService scoreService;
    @Autowired
    ScoreMapper scoreMapper;
    @Autowired
    StudentMapper studentMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/courses")
    public  String getCourses(Model model){
        System.out.println("Before PreAuthorize Check: " + SecurityContextHolder.getContext().getAuthentication()); // Added statement
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            System.out.println("After PreAuthorize Check: " + SecurityContextHolder.getContext().getAuthentication()); // Added statement
        }
       List<CourseDto> courseDtoList =  courseService.getCourses()
                       .stream().map(courseMapper::mapToCourseDto).toList();
        model.addAttribute("courseDtoList",courseDtoList);
        return"courses";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("course/new")
    public String addCourseForm(Model model){
        CourseDto courseDto = new CourseDto();
        model.addAttribute("courseDto",courseDto);
        return "addCourse";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/courses")
    public String saveCourse(@Valid @ModelAttribute("courseDto") CourseDto courseDto, BindingResult result, RedirectAttributes attributes){
        if(result.hasErrors())
            return "addCourse";
        else {
            Course course = courseMapper.mapToCourse(courseDto);
            boolean isCourseExist = courseService.isCourseExist(course.getCourseCode());
            if(isCourseExist) {
                attributes.addFlashAttribute("exist", "A course with the provided Course Code Exist");
            }
            else {
                courseService.saveCourse(course);
                attributes.addFlashAttribute("success", "Course Successfully added");
            }
            return "redirect:/courses";
        }
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @RequestMapping(value = "/course/{id}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String deleteCourse(@PathVariable Long id){
       courseService.deleteCourse(id);
       return "redirect:/courses";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/courseUpdate/{id}")
    public  String updateCourseForm(@PathVariable Long id, Model model){
        CourseDto courseDto = courseMapper.mapToCourseDto(courseService.getCourseById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found")));
        model.addAttribute("courseDto",courseDto);
        return "updateCourse";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping ("/course/update/{id}")
    public String updateCourse(@PathVariable Long id, @Valid @ModelAttribute("courseDto") CourseDto courseDto,
                               BindingResult result, RedirectAttributes attributes)
    {
        if(result.hasErrors()){
            return "updateCourse";
        }

        else {
            Course existingCourse = courseMapper.mapToCourse(courseDto);
            List<Score> existingScores = existingCourse.getScores();
            existingCourse.getScores().clear();
            existingCourse.getScores().addAll(existingScores);
           // boolean isCourseExist = courseService.isCourseExist(existingCourse.getCourseCode());
                     courseService.saveCourse(existingCourse);
                attributes.addFlashAttribute("updated", "Course Successfully updated");

            }

        return "redirect:/courses";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/courseScores/{id}")
    public String viewCourseScores(@PathVariable Long id, Model model){

        Map<ScoreDto, StudentDto> scoreDtoStudentDtoMap = new HashMap<>();
      Course course = courseService.getCourseById(id)
              .orElseThrow(() -> new CourseNotFoundException("Course Not Found"));
        model.addAttribute("course",course);
        List<Score> scores = scoreService
                .getScores()
                .stream()
                .filter(score -> score.getCourse().getId().equals(id))
                .toList();
        ScoreStat scoreStat ;

        if(scores.isEmpty() )
            throw new ScoreNotFoundException( "scores Not Found");
        else {
            for(Score score : scores)
            {
                ScoreDto scoreDto = scoreMapper.mapToScoreDto(score);
                StudentDto studentDto = studentMapper.mapToStudentDto(score.getStudent());
                scoreDto.setGrade(scoreService.getGrade(score.getScore()));
                scoreDtoStudentDtoMap.put(scoreDto, studentDto);
            }

            if(scoreService.getScoreStat(scores) == null)
                throw new ScoreNotFoundException("Score Stats not found");
            else
                scoreStat = scoreService.getScoreStat(scores);
            model.addAttribute("scoreStat", scoreStat);
            model.addAttribute("map", scoreDtoStudentDtoMap);

            return "courseScores";
        }
    }


}
