package obi.com.grademaster.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5, message = "*username must be at least 5 characters long")
    private String username;
    @Size(min = 8, message = "*Password must be at least 8 characters long")
    private String password;
    private String roles;

}
