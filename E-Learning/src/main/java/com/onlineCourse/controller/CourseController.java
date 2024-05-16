package com.onlineCourse.controller;


import com.onlineCourse.entities.*;
import com.onlineCourse.repository.CourseEnrollmentRepository;
import com.onlineCourse.service.interfaces.CourseService;
import com.onlineCourse.service.interfaces.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpSession;
import java.util.List;

@Data
@Controller
@Slf4j
public class CourseController {

	@Autowired
	UserService userService;

	@Autowired
	private CourseEnrollmentRepository courseEnrollmentRepository;

	@Autowired
	private CourseService courseService;

	@Value("${stripe.api.publicKey}")
	private String publicKey;

	@Autowired
	private WebClient.Builder webClientBuilder;

	@GetMapping("/courses")
	public String courses(HttpSession session, Model model) {
		Integer userId = getUserIdFromSession(session);
		List<Course> courseList = courseService.getCourseList(userId);
		log.info("courseList : " + courseList);
		model.addAttribute("title", "Courses");
		model.addAttribute("courseList", courseList);
		return "courses/courses";
	}

	private Integer getUserIdFromSession(HttpSession session) {
		User sessionUser = (User) session.getAttribute("user");
		return sessionUser != null ? sessionUser.getId() : null;
	}

	@PostMapping("/search")
	public String search(HttpSession session, @RequestParam(value = "searchText") String searchText, Model model) {
		Integer userId = getUserIdFromSession(session);
		log.info("Search Criteria : " + searchText);
		List<Course> courseList = courseService.search(searchText, userId);
		log.info("courseList : " + courseList);
		model.addAttribute("title", "Courses");
		model.addAttribute("courseList", courseList);
		return "courses/courses";
	}

	@RequestMapping(value = "/enroll/{id}", method = RequestMethod.GET)
	public String enrollUser(HttpSession session, @PathVariable("id") int courseId, Model model) {
		log.info("Course for Enrollment : " +  courseId);
		User sessionUser = (User) session.getAttribute("user");
		CourseEnrollment courseEnrollment = new CourseEnrollment();
		courseEnrollment.setCourseId(courseId);
		courseEnrollment.setUserId(sessionUser.getId());
		courseEnrollment.setUserName(sessionUser.getName());
		CourseEnrollment dbEnrollment = courseEnrollmentRepository.save(courseEnrollment);

		Course course = courseService.getById(courseId);

		// sending email
//		emailService.sendEmail(
//				sessionUser.getEmail(),
//				"Enrolled Successfully for " + course.getCourseName() + "Course",
//				"Dear "+sessionUser.getName()+","+"\n\n"
//						+ "Congratulations! You have successfully Enrolled to " + course.getCourseName() + " Course.\n\n"
//						+ "Thank you for choosing S3 Developments for your learning needs.\n\n"
//						+ "Best regards,\n"
//						+ "The S3 Developments Team "
//		);

		String body = "Dear "+sessionUser.getName()+","+"\n\n"
				+ "Congratulations! You have successfully Enrolled to " + course.getCourseName() + " Course.\n\n"
				+ "Thank you for choosing S3 Developments for your learning needs.\n\n"
				+ "Best regards,\n"
				+ "The S3 Developments Team ";

		//TODO: calling to email microservice
		Mono<Boolean> responseMono = webClientBuilder.build().post()
				.uri("http://EMAIL-SERVICE/email/send")
				.bodyValue(new Email(sessionUser.getEmail(), "Enrolled Successfully for " + course.getCourseName() + "Course", body)).retrieve().bodyToMono(Boolean.class);

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

		String message = "Dear "+sessionUser.getName()+","+" "
				+ "Congratulations! You have successfully Enrolled to " + course.getCourseName() + " Course. "
				+ "Thank you for choosing S3 Developments for your learning needs. "
				+ "Best regards, "
				+ "The S3 Developments Team ";

		User user = userService.getUserByEmail(sessionUser.getEmail());
		// sending sms
		log.info("dest phone {}", user.getPhoneNumber());

//		SmsService smsService = new NotifySmsServiceImpl();
//
//		smsService.sendSms(new SmsRequest(
//				user.getPhoneNumber(),
//				message
//		));

		//TODO: calling to sms microservice
		Mono<Boolean> responseMono2 = webClientBuilder.build().post()
				.uri("http://SMS-SERVICE/sms/send")
				.bodyValue(new SmsRequest(user.getPhoneNumber(), message)).retrieve().bodyToMono(Boolean.class);

		responseMono2.subscribeOn(Schedulers.elastic())
				.subscribe(
						result -> {
							// Handle the result here
							System.out.println("sms sent successfully: " + result);
						},
						error -> {
							// Handle any errors
							System.err.println("Failed to send sms: " + error.getMessage());
						}
				);

		model.addAttribute("success", sessionUser.getName() + " successfully enrolled for courseId : " + courseId);
		log.info("success" +  sessionUser.getName() + " successfully enrolled for courseId : " + courseId);
		return myCourses(session, model);
	}

	@GetMapping(value = "/init-add-course")
	public String initAddCourse(Model model) {
		model.addAttribute("course", new Course());
		model.addAttribute("title", "Add Course");
		log.info("loading init-add-course..!");
		return "courses/add-course";
	}

	@RequestMapping(value = "/submit-add-course", method = RequestMethod.POST)
	public String submitAddCourse(@ModelAttribute("course") Course course, Model model) {
		Course dbCourse = courseService.save(course);
		model.addAttribute("success", dbCourse.getCourseName() + " added successfully. You can continue to add more..!");
		log.info("success - " + course.getCourseName() + " added successfully. Course =  : " + dbCourse);
		return initAddCourse(model);
	}

	@RequestMapping(value = "/init-manage-course", method = RequestMethod.POST)
	public String initManageCourse(@ModelAttribute("course") Course course, Model model) {
		model.addAttribute("course", course);
		model.addAttribute("title", "Manage Course");
		log.info("loading init-manage-course..! course=" + course);
		return "courses/manage-course";
	}

	@RequestMapping(value = "/submit-manage-course", method = RequestMethod.POST)
	public String submitManageCourse(HttpSession session, @ModelAttribute("course") Course course, Model model) {
		log.info("course=" + course);
		if(course.getId()>0){
			Course dbCourse = courseService.save(course);
			model.addAttribute("success", dbCourse.getCourseName() + " updated successfully.");
			return courses(session, model);
		}
		model.addAttribute("error", course.getCourseName() + " update failed.");
		return courses(session, model);
	}

	@RequestMapping(value = "/delete-course/{id}", method = RequestMethod.GET)
	public String deleteCourse(HttpSession session, @PathVariable("id") int id, Model model) {
		log.info("Id=" + id);
		if(id>0){
			try {
				courseService.deleteById(id);
				model.addAttribute("success", "Course with " + id + " deleted successfully.");
			} catch (Exception e) {
				log.error("Error : " + e.getLocalizedMessage());
				model.addAttribute("error", "Course with id : " + id + " delete failed with exception.");			}
			return courses(session, model);
		}
		model.addAttribute("error", "Course with id : " + id + " delete failed.");
		return courses(session, model);
	}

	@GetMapping("/my-courses")
	public String myCourses(HttpSession session,  Model model) {
		User sessionUser = (User) session.getAttribute("user");
		List<Course> courseList = courseService.getEnrolledCourseList(sessionUser.getId());
		log.info("courseList"+courseList);
		log.info("courseList : " + courseList);
		if(!courseList.isEmpty()) {
			model.addAttribute("title", "My Courses");
			model.addAttribute("courseList", courseList);
			return "courses/courses";
		}
		model.addAttribute("message","Not Enrolled for any courses");
		System.out.println("not enrolled");
		return "courses/courses";
	}

	@RequestMapping(value = "/init-course-detail", method = RequestMethod.POST)
	public String courseDetail(HttpSession session, @ModelAttribute  Course course,  Model model) {
		model.addAttribute("course", course);
		model.addAttribute("title", "Course Detail");
		log.info("loading init-course-detail..! course=" + course);
		return "courses/course-details" ;
	}

	@RequestMapping(value = "/payment/{id}", method = RequestMethod.GET)
	public String showCard(HttpSession session,
						   @PathVariable("id") int courseId,
						   Model model){
		User sessionUser = (User) session.getAttribute("user");
		Course course = courseService.getById(courseId);

		// to skip amount = 0 error
		if(course.getPrice() < 1){
			course.setPrice(10);
		}

		model.addAttribute("publicKey", publicKey);
		model.addAttribute("amount", course.getPrice());
		model.addAttribute("email", sessionUser.getEmail());
		model.addAttribute("courseName", course.getId());
		return "checkout";
	}

}
