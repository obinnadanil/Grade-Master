package obi.com.grademaster.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreStat{
    private int highestScore;
    private int lowestScore;
    private double averageScore;
}