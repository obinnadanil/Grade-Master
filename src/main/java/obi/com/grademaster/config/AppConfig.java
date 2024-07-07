package obi.com.grademaster.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import obi.com.grademaster.entity.CustomUserDetails;
import obi.com.grademaster.entity.Student;
import obi.com.grademaster.exception.StudentNotFoundException;
import obi.com.grademaster.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class AppConfig {
    @Autowired
    StudentService studentService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((request) -> request
                .requestMatchers("/","/home","/addAdmin","/registerStudent","/css/**","/registerStudent").permitAll()
                        .anyRequest()
                        .authenticated())

                .formLogin((form) -> form.loginPage("/login").successHandler(((request, response, authentication) -> {
                    String targetUrl =successUrl(authentication,request);
                    response.sendRedirect(targetUrl);
                        }))
                        .permitAll())
                .logout((logout) -> logout.logoutUrl("/logout").permitAll())
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer
                        -> httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> response.sendRedirect("accessDenied");
    }
    public String successUrl(Authentication authentication, HttpServletRequest request) {

        if (hasRole(authentication, "ADMIN")) {

            return "/";
        } else {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String regNumber = customUserDetails.getUsername();

            Student student = studentService.getStudentByRegNumber(regNumber)
                    .orElseThrow(() -> new StudentNotFoundException("StudentNotFound"));
            HttpSession session = request.getSession();
            session.setAttribute("studentDetails", student);
            return "/studentPortal?id="+student.getId();
        }
    }



    public boolean hasRole(Authentication authentication, String role){
        return authentication.
                getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_"+role));
    }

}
