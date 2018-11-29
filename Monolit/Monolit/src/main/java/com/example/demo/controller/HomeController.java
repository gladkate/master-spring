package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = {"/","/home"},  method = {RequestMethod.GET, RequestMethod.POST })
    public ModelAndView home(Locale locale, ModelAndView modelAndView) {

        logger.info("Welcome home! The client locale is {}.", locale);
        modelAndView.setViewName("home");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        modelAndView.addObject("date", dateFormat.format(cal.getTime()));
        return modelAndView;
    }
}
