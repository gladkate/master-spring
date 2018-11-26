package com.example.demo.controller;

import com.example.demo.configuration.Messages;
import com.project.core.model.User;
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

    private String uri = "http://localhost:9999/project/v1/login/";

    @Autowired
    private RestTemplate restTemplate;

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

            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            String URL = uri + "validateUser/" + email + "/" + password;
            boolean passedValidation = false;
            try {
                passedValidation = restTemplate.getForObject(URL, boolean.class, params);
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
//        Map<String, String> params = new HashMap<>();
//        params.put("email", auth.getName());
//        String email = auth.getName();
//        User user = restTemplate.getForObject(uri + "getUserByEmail/"  + auth.getName(), User.class, params);

        getUserByEmail("dupa");
        String url = "http://localhost:9999/project/v1/user/getUserByEmail/{email}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(auth.getName(), headers);
        ResponseEntity<User> user = restTemplate.exchange(url, HttpMethod.GET, entity, User.class, auth.getName());

        //modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        //modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @RequestMapping(value = "/logowanie", method = RequestMethod.GET)
    public ModelAndView logowanie() {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String url = "http://localhost:9999/project/v1/getUserByEmail/{email}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(auth.getName(), headers);
        ResponseEntity<User> user = restTemplate.exchange(url, HttpMethod.GET, entity, User.class, auth.getName());

        modelAndView.addObject("userName", "Welcome " + user.getBody().getFirstName() +
                " " + user.getBody().getLastName() + " (" + user.getBody().getEmail() + ")");
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.setViewName("home");
        return modelAndView;
    }

    public User getUserByEmail(String email) {
        String url = "http://localhost:9999/project/v1/getUserByEmail/{email}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(email, headers);
        ResponseEntity<User> user = restTemplate.exchange(url, HttpMethod.GET, entity, User.class, email);
        return user.getBody();
    }



}
