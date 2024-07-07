package obi.com.grademaster.DTO;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDto {
    private Long id;

    private String courseName;
    @NotBlank(message = "required")
    private String courseCode;
    @Max(value = 50, message = "max score is 50")
    private int score;
    @NotBlank(message = "required")
    @Size(min = 11, max = 11)
    private String regNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;
    private String grade;


}
