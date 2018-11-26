package com.example.demo.service;

import com.example.demo.configuration.Messages;
import com.project.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service("emailService")
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    private Messages messages;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(SimpleMailMessage email) {
        mailSender.send(email);
    }

    public SimpleMailMessage registrationConfirmationEmailMessage(User user, String appUrl, Locale locale){
        SimpleMailMessage registrationEmail = new SimpleMailMessage();
        registrationEmail.setTo(user.getEmail());
        registrationEmail.setSubject(messages.get("email.subject", locale));
        registrationEmail.setText(messages.get("email.message", locale)
                + appUrl + "/confirm?token=" + user.getConfirmationToken());
        registrationEmail.setFrom("noreply@domain.com");
        return registrationEmail;
    }

    public void sendConfirmationEmail(User user, String appUrl, Locale locale){
        SimpleMailMessage registrationEmail = registrationConfirmationEmailMessage(user, appUrl, locale);
        sendEmail(registrationEmail);
    }
}