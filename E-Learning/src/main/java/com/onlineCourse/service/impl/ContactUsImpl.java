package com.onlineCourse.service.impl;

import com.onlineCourse.entities.ContactUs;
import com.onlineCourse.repository.ContactUsRepository;
import com.onlineCourse.service.interfaces.ContactUsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@Slf4j
public class ContactUsImpl implements ContactUsService {
    @Autowired
    private ContactUsRepository contactUsRepository;

    public void submitMessage(ContactUs contactUs, Model model) {
        contactUsRepository.save(contactUs);
        model.addAttribute("success", "Message sent successfully.");
        log.info("Message sent successfully.");
    }
}
