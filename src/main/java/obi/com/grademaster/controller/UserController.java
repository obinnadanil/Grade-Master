package obi.com.grademaster.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import obi.com.grademaster.entity.User;
import obi.com.grademaster.service.UserSecurityUserDetailService;
import obi.com.grademaster.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@EnableMethodSecurity
public class UserController {

    @Autowired
    UserSecurityUserDetailService service;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    StudentService studentService;
    SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/addAdmin")
    public String addAdminForm(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "addAdminForm";
    }
    @GetMapping("/registerStudent")
    public String registerStudent(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "registerStudent";
    }
    @PreAuthorize("hasAuthority(ROLE_ADMIN)")
    @PostMapping("/addAdmin")
    public String saveAdmin(@Valid @ModelAttribute("user") User user, BindingResult result, Model attributes){
       if (result.hasErrors())
           return "addAdminForm";
       else {
           if (service.isAdminExist(user.getUsername())) {
               attributes.addAttribute
                       ("fail", "Username not available, change your username");
               return "failed";
           } else {
               User newUser = new User();
               newUser.setUsername(user.getUsername());
               newUser.setPassword(encoder.encode(user.getPassword()));
               newUser.setRoles("ADMIN");
               service.addUser(newUser);
               return "redirect:/";
           }
       }
    }
    @PostMapping("/registerStudent")
    public String saveStudentLoginDetails(@ModelAttribute("user") User user, Model attributes){
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setRoles("STUDENT");
            String regNumber = newUser.getUsername();
            boolean isStudentExist = studentService.isStudentExist(regNumber);
            boolean isUsernameExist = service.getUsers().stream()
                    .anyMatch(user1 -> user1.getUsername().equals(newUser.getUsername()));
            if(isStudentExist && !isUsernameExist) {
                service.addUser(newUser);
                return "success";
            }
            else {
                attributes.addAttribute("studentRegistrationFailed",
                        "The registration number does not exist, or you have previously registered");
                return "failed";
            }
    }
    @PreAuthorize("hasAuthority('ROLE_STUDENT')" )
    @GetMapping("/studentPortal")
    public String showStudentPortal(){
        return "studentPortal";
    }
    @GetMapping("/accessDenied")
    public String showAccessDeniedPage(){
        return "accessDenied";
    }
    @GetMapping("/login")
    public  String showLoginPage(Model model){
        return "login";
    }

    @GetMapping("/logout")
    public String performLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        this.logoutHandler.logout(request, response, authentication);
        return "redirect:/login";
    }

}
