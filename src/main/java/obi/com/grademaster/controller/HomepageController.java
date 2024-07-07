package obi.com.grademaster.controller;

import obi.com.grademaster.entity.Course;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.entity.Student;
import obi.com.grademaster.service.CourseService;
import obi.com.grademaster.service.ScoreService;
import obi.com.grademaster.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomepageController {
    @Autowired
    ScoreService scoreService;
    @Autowired
    StudentService studentService;
    @Autowired
    CourseService courseService;
    @GetMapping("/")
    public  String homepage(Model model){
        List<Score> scoreList = scoreService.getScores();
        List<Student> studentList = studentService.getStudents();
        List<Course> courseList = courseService.getCourses();
        model.addAttribute("scoreList",scoreList);
        model.addAttribute("studentList",studentList);
        model.addAttribute("courseList",courseList);

        return  "homepage";
    }

}
