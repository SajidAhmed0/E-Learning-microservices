package com.onlineCourse.controller;

import com.onlineCourse.entities.Email;
import com.onlineCourse.entities.User;
import com.onlineCourse.service.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.thymeleaf.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CourseController courseController;
    @Autowired
    private WebClient.Builder webClientBuilder;


    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(HttpSession session, @ModelAttribute("user") User user, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model) {
//        if (!agreement) {
//            log.info("You have not agreed the terms and conditions.");
//            model.addAttribute("error", "You have not agreed the terms and conditions.");
//            return "sign-up";
//        }
        log.info("USER " + user);
        boolean isDuplicateUser = userService.isUserAlreadyExists(user.getEmail());

        if(isDuplicateUser){
            model.addAttribute("error", "User already exists in database.");
            log.info("User already exists in database.");
            return "sign-up";
        }
        User dbUser = userService.save(user);
        session.setAttribute("user", dbUser);
        model.addAttribute("user", dbUser);
        session.setAttribute("name", dbUser.getName());
        log.info("dbuser phone {}" + dbUser.getPhoneNumber());

//        emailService.sendEmail(dbUser.getEmail(),
//                "Registration Successful",
//                "Dear "+user.getName()+","+"\n\n"
//                        + "Congratulations! You have successfully Registered to S3 Developments.\n\n"
//                        + "Thank you for choosing S3 Developments for your learning needs.\n\n"
//                        + "Best regards,\n"
//                        + "The S3 Developments Team ");

        String body = "Dear "+user.getName()+","+"\n\n"
                + "Congratulations! You have successfully Registered to S3 Developments.\n\n"
                + "Thank you for choosing S3 Developments for your learning needs.\n\n"
                + "Best regards,\n"
                + "The S3 Developments Team ";

        //TODO: calling to email microservice
        Mono<Boolean> responseMono = webClientBuilder.build().post()
                .uri("http://EMAIL-SERVICE/email/send")
                .bodyValue(new Email(dbUser.getEmail(), "Registration Successful", body)).retrieve().bodyToMono(Boolean.class);

        responseMono.subscribeOn(Schedulers.elastic())
                .subscribe(
                        result -> {
                            // Handle the result here
                            System.out.println("Email sent successfully: " + result);
                        },
                        error -> {
                            // Handle any errors
                            System.err.println("Failed to send email: " + error.getMessage());
                        }
                );


//        model.addAttribute("info", "Welcome "+ dbUser.getName() + "!");
//        model.addAttribute("success", "User registered successfully.");
        log.info("User "+ dbUser.getName() + " successfully Registered.");
        return "index";
    }

    @GetMapping(value = "/profile")
    public String profile(HttpSession session, Model model) {
        model.addAttribute("title", "Profile");
        User sessionUser = (User) session.getAttribute("user");
        model.addAttribute("user", sessionUser);
        return "profile";
    }

    @RequestMapping(value = "/update-profile", method = RequestMethod.POST)
    public String updateUser(HttpSession session, @ModelAttribute("user") User user, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        log.info("USER : " + user);
        log.info("sessionUser : " + user);
        if(StringUtils.equalsIgnoreCase(sessionUser.getEmail(),user.getEmail())){
            user.setId(sessionUser.getId());
            if(StringUtils.isEmpty(user.getPassword())){
                user.setPassword(sessionUser.getPassword());
            }
            userService.save(user);
            session.setAttribute("user", user);
            session.setAttribute("name", user.getName());
            model.addAttribute("success", "Profile updated successfully.");
            log.info("Profile updated successfully.");
            return "index";
        }
        model.addAttribute("error", "Email update not allowed.");
        log.info("Email update not allowed.");
        return "profile";
    }

    @GetMapping(value = "/delete-profile")
    public String deleteUser(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        log.info("sessionUser : " + sessionUser);
        userService.deleteById(sessionUser.getId());
        session.invalidate();
        model.addAttribute("success", "Profile deleted successfully.");
        log.info("Profile deleted successfully.");
        return "index";
    }

}
