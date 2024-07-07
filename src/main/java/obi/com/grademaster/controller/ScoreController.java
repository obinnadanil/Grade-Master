package obi.com.grademaster.controller;

import jakarta.validation.Valid;
import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.exception.StudentNotFoundException;
import obi.com.grademaster.mapper.ScoreMapper;
import obi.com.grademaster.service.CourseService;
import obi.com.grademaster.service.ScoreService;
import obi.com.grademaster.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@EnableMethodSecurity
@Controller
public class ScoreController {
    @Autowired
    ScoreService scoreService;
    @Autowired
    StudentService studentService;
    @Autowired
    CourseService courseService;
    @Autowired
    ScoreMapper scoreMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/scores")
    public String getScores(Model model) {

        List<ScoreDto> scoreDtoList = scoreService.getScores()
                .stream().map(scoreMapper::mapToScoreDto).toList();
        model.addAttribute("scoreDtoList", scoreDtoList);
        return "scores";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/score/new")
    public String addScoreForm(Model model) {
        ScoreDto scoreDto = new ScoreDto();
        model.addAttribute("scoreDto", scoreDto);
        model.addAttribute("courses", courseService.getCourses());
        return "addScore";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/scores")
    public String saveScore(@Valid @ModelAttribute("scoreDto") ScoreDto scoreDto,
                            BindingResult result, Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.getCourses());
            return "addScore";
        } else {
            Score score = scoreMapper.mapToScore(scoreDto);
            if (scoreService.isScoreExist(score)) {
                attributes.addFlashAttribute("failed","Score already added" );
            }
            else {
                scoreService.saveScore(score);
                attributes.addFlashAttribute("success", "Score successfully added");
            }
            return "redirect:/scores";
        }

    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "score/{id}", method = {RequestMethod.GET, RequestMethod.DELETE})
    public String deleteScore(@PathVariable Long id) {
        scoreService.deleteScore(id);
        return "redirect:/scores";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/score/update/{id}")
    public String updateScoreForm(@PathVariable Long id, Model model){
        ScoreDto existingScoreDto = scoreMapper.mapToScoreDto(scoreService.getScoreById(id)
                .orElseThrow(() -> new StudentNotFoundException("scoreNotFound")));
        model.addAttribute("scoreDto", existingScoreDto);
        model.addAttribute("courses", courseService.getCourses());
        return "updateScore";

    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/score/{id}")
    public  String updateScore(@PathVariable Long id,@Valid @ModelAttribute("scoreDto") ScoreDto scoreDto,
                               BindingResult result,Model model,RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("courses", courseService.getCourses());
            return "updateScore";
        }
        else{
            Score existingScore = scoreMapper.mapToScore(scoreDto);
             scoreService.saveScore(existingScore);
            attributes.addFlashAttribute("updated", "Score Successfully updated");
    }
        return "redirect:/scores";
    }


}
