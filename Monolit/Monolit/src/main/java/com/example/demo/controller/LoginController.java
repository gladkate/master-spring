package com.example.demo.controller;

import com.example.demo.configuration.Messages;
import com.example.demo.model.User;
import com.example.demo.service.LoginService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
@SessionAttributes("user")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserService userService;

    @Autowired
    private Messages messages;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLoginPage(Locale locale, ModelAndView modelAndView, String email, String password) {
        modelAndView.addObject("email", email);
        modelAndView.addObject("password", password);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView showWelcomePage(Authentication authentication, Locale locale, ModelAndView modelAndView, @Valid @RequestParam String email, @Valid @RequestParam String password, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("wrongCredentials", messages.get("login.wrongCredentials", locale));
            return modelAndView;
        } else {

            boolean passedValidation = false;
            try {
                passedValidation = loginService.validateUser(email,password);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!passedValidation) {
                modelAndView.addObject("errorMessage", messages.get("login.errorMessage", locale));
                return modelAndView;
            } else {
                boolean isLogged = true;
                ModelAndView modelAndViewWelcomePage = new ModelAndView();
                modelAndViewWelcomePage.addObject("isLogged", isLogged);
                modelAndViewWelcomePage.addObject("welcomeMessage", messages.get("login.welcomeMessage", locale));
                modelAndViewWelcomePage.setViewName("home");
                return modelAndViewWelcomePage;
            }
        }
    }

    @RequestMapping(value = "/loginhome", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getFirstName() + "!");
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping(value = "/logowanie", method = RequestMethod.GET)
    public ModelAndView logowanie() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        User user = userService.getUserByEmail(auth.getName());

        modelAndView.addObject("userName", "Welcome " + user.getFirstName() +
                " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.setViewName("home");
        return modelAndView;
    }


}
