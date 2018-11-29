package com.example.demo.controller;

import com.example.demo.configuration.Messages;
import com.example.demo.model.User;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
@ComponentScan("com.example")
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    Messages messages;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(Locale locale, ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(Locale locale, ModelAndView modelAndView, @Valid User user, HttpServletRequest request) {

        User userExists = userService.getUserByEmail(user.getEmail());

        if (userExists.getEmail() != null) {
            modelAndView.addObject("alreadyRegisteredMessage", messages.get("register.alreadyRegistered", locale));
            modelAndView.addObject("sent", false);
            modelAndView.setViewName("register");
        }
        else { // new user
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
            userService.registerUserAndSendEmail(user, appUrl, locale);
            modelAndView.addObject("confirmationMessage", messages.get("register.confirmationMessage", locale) + user.getEmail());
            modelAndView.addObject("sent", true);
            modelAndView.setViewName("register");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ModelAndView showConfirmationPage(Locale locale, ModelAndView modelAndView, @RequestParam("token") String token) {

        User user = userService.getUserByConfirmationToken(token);
        if (user.getConfirmationToken() == null) { // No token found in DB
            modelAndView.addObject("invalidToken", messages.get("register.invalidToken", locale));
        } else { // Token found
            modelAndView.addObject("confirmationToken", user.getConfirmationToken());
        }

        modelAndView.setViewName("confirm");
        return modelAndView;
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public ModelAndView processConfirmationForm(Locale locale, ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map requestParams, RedirectAttributes redir) {

        modelAndView.setViewName("confirm");

        Zxcvbn passwordCheck = new Zxcvbn();

        Strength strength = passwordCheck.measure((String) requestParams.get("password"));

        if (strength.getScore() < 3) {
            bindingResult.reject("password");

            redir.addFlashAttribute("errorMessage", messages.get("register.errorMessage", locale));

            modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
            System.out.println(requestParams.get("token"));
            return modelAndView;
        }

        User user = userService.getUserByConfirmationToken((String) requestParams.get("token"));

        user.setPassword(bCryptPasswordEncoder.encode((CharSequence) requestParams.get("password")));
        user.setEnabled(true);

        userService.updateUser(user);

        modelAndView.addObject("successMessage", messages.get("register.successMessage", locale));
        modelAndView.addObject("saved", true);
        return modelAndView;
    }
}