package obi.com.grademaster.controller;

import jakarta.validation.Valid;
import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.DTO.ScoreStat;
import obi.com.grademaster.DTO.StudentDto;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.entity.Student;
import obi.com.grademaster.exception.ScoreNotFoundException;
import obi.com.grademaster.exception.StudentNotFoundException;
import obi.com.grademaster.mapper.ScoreMapper;
import obi.com.grademaster.mapper.StudentMapper;
import obi.com.grademaster.service.CourseService;
import obi.com.grademaster.service.ScoreService;
import obi.com.grademaster.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    StudentService studentService;
    @Autowired
    ScoreService scoreService;
    @Autowired
    CourseService courseService;
    @Autowired
    ScoreMapper scoreMapper;
    @Autowired
    StudentMapper studentMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/students")
    public String getStudents(Model model) {
        model.addAttribute("studentDtoList", studentService.getStudentDtoList());
        return "students";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/students/new")
    public String studentForm(Model model) {
        StudentDto studentDto = new StudentDto();
        model.addAttribute("studentDto", studentDto);
        return "addStudent";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/students")
    public String addStudent(@Valid @ModelAttribute("studentDto") StudentDto studentDto,
            BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors())
            return "addStudent";
        else {
            Student student = studentMapper.mapToStudent(studentDto);
            student.setRegNumber(studentService.generateRegNumber());
            studentService.saveStudent(student);
            attributes.addFlashAttribute("success", "Student Successfully added");
            return "redirect:/students";
        }
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/student/{id}", method = { RequestMethod.GET, RequestMethod.DELETE })
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')"  + "||hasAuthority('ROLE_STUDENT')")
    @GetMapping("/studentResult/{id}")
    public String viewStudentResult(@PathVariable Long id, Model model) {

        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("No student Found"));
        model.addAttribute("student", student);
        ScoreStat scoreStat;
        List<Score> scoreList = scoreService.getScoresByStudentId(id, scoreService.getScores());

        if (scoreList.isEmpty())
            throw new ScoreNotFoundException("scores Not Found");
        else {

            List<ScoreDto> scoreDtoList = new ArrayList<>();
            for (Score score : scoreList) {
                ScoreDto scoreDto = scoreMapper.mapToScoreDto(score);
                String grade = scoreService.getGrade(scoreDto.getScore());
                scoreDto.setGrade(grade);
                scoreDtoList.add(scoreDto);
            }

            if (scoreService.getScoreStat(scoreList) == null)
                throw new ScoreNotFoundException("Score Stats not found");
            else
                scoreStat = scoreService.getScoreStat(scoreList);

            model.addAttribute("scoreStat", scoreStat);
            model.addAttribute("scoreDtoList", scoreDtoList);
            return "studentResult";
        }
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/student/update/{id}")
    public String updateStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));

        model.addAttribute("studentDto", studentMapper.mapToStudentDto(student));
        return "updateStudent";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')" )
    @PostMapping("/student/update/{id}")
    public String updateStudent(@PathVariable Long id, @Valid @ModelAttribute("studentDto") StudentDto studentDto,
            BindingResult result, RedirectAttributes attributes) {
        if (studentService.isStudentExist(studentDto.getRegNumber()))
            attributes.addFlashAttribute("exist", "Student already exist");
        if (result.hasErrors()) {
            return "updateStudent";

        } else {
            Student existingStudent = studentMapper.mapToStudent(studentDto);
            List<Score> updatedScores = scoreService.getScoresByStudentId(id, scoreService.getScores());
            existingStudent.getScores().clear();
            if (updatedScores != null) {
                existingStudent.getScores().addAll(updatedScores);

            }
            studentService.saveStudent(existingStudent);
            attributes.addFlashAttribute("updated", "Student Successfully updated");

        }
        return "redirect:/students";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')" + "||hasAuthority('ROLE_STUDENT')")
    @GetMapping("/studentResultPdf/{id}")
    public ResponseEntity<byte[]> printResult(@PathVariable Long id) throws IOException {

        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found"));
        List<ScoreDto> scoreDtoList = scoreService.convertStudentScoresToStudentDtoList(id, scoreMapper);
        ByteArrayOutputStream pdfContent = studentService.generatePdf(id, scoreDtoList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", student.getFirstName() + ".pdf");

        return new ResponseEntity<>(pdfContent.toByteArray(), headers, HttpStatus.OK);
    }



}
