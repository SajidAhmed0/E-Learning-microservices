package com.onlineCourse.controller;
import com.onlineCourse.entities.Email;
import com.onlineCourse.entities.User;
import com.onlineCourse.repository.ContactUsRepository;

import com.onlineCourse.service.interfaces.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;
import reactor.core.scheduler.Schedulers;

@Data
@Controller
@Slf4j
public class LoginController {

	@Autowired
	private ContactUsRepository contactUsRepository;
	@Autowired
	private UserService userService;

	@Autowired
	private CourseController courseController;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Sign Up");
		model.addAttribute("user", new User());
		return "sign-up";
	}
	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("title", "Login");
		model.addAttribute("user", new User());
		return "login";
	}

	@RequestMapping(value = "/do_login", method = RequestMethod.POST)
	public String loginUser(HttpSession session, @ModelAttribute("user") User user, Model model) {

		log.info("USER : " + user);
		boolean isValidUser = userService.isValidUser(user);

		if(isValidUser){
			log.info("Session User :  " + user);
			model.addAttribute("user ", user);
			session.setAttribute("user", user);
			session.setAttribute("name", user.getName());
//			emailService.sendEmail(user.getEmail(),
//					"Login Successful",
//					"Dear "+user.getName()+","+"\n\n"
//							+ "Congratulations! You have successfully logged in to S3 Development.\n\n"
//							+ "Thank you for choosing S3 Developments for your learning needs.\n\n"
//							+ "Best regards,\n"
//							+ "The S3 Developments Team ");

			//TODO: calling to email microservice
			Mono<Boolean> responseMono = webClientBuilder.build().post()
					.uri("http://EMAIL-SERVICE/email/send")
							.bodyValue(new Email(user.getEmail(), "Login Successful", "Dear "+user.getName()+","+"\n\n"
									+ "Congratulations! You have successfully logged in to S3 Development.\n\n"
									+ "Thank you for choosing S3 Developments for your learning needs.\n\n"
									+ "Best regards,\n"
									+ "The S3 Developments Team ")).retrieve().bodyToMono(Boolean.class);

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
			//model.addAttribute("info", "Welcome "+ user.getName() + "!");
			log.info("Welcome "+ user.getName() + "!");
			return "index";
		}
		log.info("Invalid email/password.");
		model.addAttribute("error", "Invalid email/password.");
		return "login";
	}

	@GetMapping(value = "/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("success", "Logged out successfully.");
		log.info("Logged out successfully.");
		return "index";
	}

}
