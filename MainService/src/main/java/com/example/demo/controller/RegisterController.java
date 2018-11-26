package com.example.demo.controller;

import com.example.demo.configuration.Messages;
import com.example.demo.service.EmailService;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.project.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
@ComponentScan("com.example")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private String uri = "http://localhost:9999/project/v1/user/";

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    Messages messages;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage(Locale locale, ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView processRegistrationForm(Locale locale, ModelAndView modelAndView, @Valid User user,
                                                HttpServletRequest request) {

        Map<String, String> params = new HashMap<>();
        params.put("email", user.getEmail());
        User userExists = restTemplate.getForObject(uri + "getUserByEmail/" + user.getEmail(), User.class, params);

        if (userExists.getEmail() != null) {
            modelAndView.addObject("alreadyRegisteredMessage",
                    messages.get("register.alreadyRegistered", locale));
            modelAndView.setViewName("register");
            modelAndView.addObject("sent", false);
            logger.info("User o podanym loginie istnieje: {}", userExists.getEmail());
        } else { // new user
            //prepare to send request via RestTemplate
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity userEntity = new HttpEntity(user, headers);
            ResponseEntity<User> response = restTemplate.postForEntity(uri + "saveUser", userEntity, User.class);
            String appUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort();
            emailService.sendConfirmationEmail(response.getBody(), appUrl, locale);

            modelAndView.addObject("confirmationMessage",
                    messages.get("register.confirmationMessage", locale) + user.getEmail());
            modelAndView.addObject("sent", true);
            modelAndView.setViewName("register");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ModelAndView showConfirmationPage(Locale locale, ModelAndView modelAndView, @RequestParam("token") String token) {

        Map<String, String> params = new HashMap<>();
        params.put("confirmationToken", token);

        User user = restTemplate.getForObject(uri + "getUserByConfirmationToken/" + token, User.class, params);

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

        Map<String, String> params = new HashMap<>();
        params.put("confirmationToken", (String) requestParams.get("token"));

        String url = uri + "getUserByConfirmationToken/" + params.get("confirmationToken");
        User user = restTemplate.getForObject(url, User.class, params);

        user.setPassword(bCryptPasswordEncoder.encode((CharSequence) requestParams.get("password")));
        user.setEnabled(true);

        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //set your entity to send        //set your entity to send
        HttpEntity userEntity = new HttpEntity(user, headers);

        ResponseEntity<User> response = restTemplate.postForEntity(uri + "saveUser", userEntity, User.class);

        modelAndView.addObject("successMessage", messages.get("register.successMessage", locale));
        modelAndView.addObject("saved", true);
        return modelAndView;
    }
}