package obi.com.grademaster.service;

import obi.com.grademaster.DTO.ScoreDto;
import obi.com.grademaster.DTO.ScoreStat;
import obi.com.grademaster.entity.Score;
import obi.com.grademaster.exception.ScoreNotFoundException;
import obi.com.grademaster.mapper.ScoreMapper;
import obi.com.grademaster.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class ScoreService {

    @Autowired
    ScoreRepository scoreRepository;


    public List<Score> getScores(){
        return scoreRepository.findAll();
    }
    public Optional<Score> getScoreById(Long id){
       return scoreRepository.findById(id);

    }

    public  void saveScore(Score score){
        scoreRepository.save(score);
    }

    public  void deleteScore(Long id){
        scoreRepository.deleteById(id);
    }
    public Date setRegistrationDate(){
        return new Date();
    }
    public Score getMaxScore(List<Score> scores){
        return scores.stream().max(Comparator.comparing(Score::getScore))
                .orElseThrow(() -> new ScoreNotFoundException("No lowest Score Found"));
    }
    public Score getMinScore(List<Score> scores){
        return scores.stream().min(Comparator.comparing(Score::getScore))
                .orElseThrow(() -> new ScoreNotFoundException("No Highest Score Found"));
    }
    public List<Score> getScoresByStudentId(Long id, List<Score> scores){
       return scores.stream().filter(score -> score.getStudent().getId().equals(id)).toList();
    }
    public List<Score> getScoresByCourseId(Long id, List<Score> scores){
        return scores.stream().filter(score -> score.getCourse().getId().equals(id)).toList();
    }
    public double getAverageScore(List<Score> scores){
   return scores.stream().mapToDouble(Score::getScore)
           .average().orElse(0.0);
    }

    public List<ScoreDto> convertStudentScoresToStudentDtoList(Long id, ScoreMapper scoreMapper){
        List<Score> scoreList = getScoresByStudentId(id, getScores());
        List<ScoreDto> scoreDtoList = new ArrayList<>();
        for(Score score: scoreList){
            ScoreDto scoreDto = scoreMapper.mapToScoreDto(score);
            scoreDto.setGrade(getGrade(score.getScore()));
            scoreDtoList.add(scoreDto);
        }
        return scoreDtoList;
    }
    public ScoreStat getScoreStat(List<Score> scoreList){
        ScoreStat scoreStat = new ScoreStat();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        scoreStat.setAverageScore(Double.parseDouble(decimalFormat.format(getAverageScore(scoreList))));
        scoreStat.setHighestScore(getMaxScore(scoreList).getScore());
        scoreStat.setLowestScore(getMinScore(scoreList).getScore());
        return scoreStat;
    }
    public void updateScore(Score score){
        scoreRepository.save(score);
    }


   public boolean isScoreExist(Score score){
        String courseCode = score.getCourse().getCourseCode();
        String regNumber = score.getStudent().getRegNumber();
        return  scoreRepository.existsByCourseCodeAndRegNumber(courseCode,regNumber);

   }
   public String getGrade(int score){
        double percentage =  score * 100/50.0;
        if (percentage >= 70)
            return "A";
        else
            if (percentage >= 60)
                return "B";
        else
            if (percentage >= 50)
                return "C";
        else
            if (percentage >= 45)
                return "D";

        else return "F";
   }
}
