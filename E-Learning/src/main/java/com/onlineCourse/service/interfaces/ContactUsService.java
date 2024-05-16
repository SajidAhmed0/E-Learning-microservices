package com.onlineCourse.service.interfaces;

import com.onlineCourse.entities.ContactUs;
import org.springframework.ui.Model;

public interface ContactUsService {

    public void submitMessage(ContactUs contactUs, Model model);
}
