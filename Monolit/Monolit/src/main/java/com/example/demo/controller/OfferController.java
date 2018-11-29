package com.example.demo.controller;

import com.example.demo.model.Offer;
import com.example.demo.model.User;
import com.example.demo.service.OfferService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class OfferController {

    @Autowired
    private UserService userService;

    @Autowired
    private OfferService offerService;

    @RequestMapping(value = "/addOffer", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage( Locale locale, ModelAndView modelAndView, Offer offer) {
        modelAndView.addObject("offer", offer);
        modelAndView.setViewName("offerform");
        return modelAndView;
    }

    @RequestMapping(value = "/addOffer", method = RequestMethod.POST)
    public String processRegistrationForm(Authentication authentication, Locale locale, ModelAndView modelAndView, @Valid Offer offer, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "offerform";
        }
        User user= userService.getUserByEmail(authentication.getName());

        offer.setCustomer_id(user.getId());
        offerService.createOffer(offer);
        return "redirect:/offer/show/" + offer.getId();
    }


    @RequestMapping(value = {"/offer/list", "/offer"}, method = RequestMethod.GET)
    public String listProducts(Locale locale, Model model) {

        List<Offer> offersList = offerService.getAllOffers();
        offersList = offersList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        model.addAttribute("offers", offersList);
        return "offerlist";
    }

    @RequestMapping("/offer/show/{id}")
    public String getProduct(Locale locale, @PathVariable int id, Model model) {

        Offer offer = offerService.getOfferById(id).get();
        model.addAttribute("offer", offer);
        return "offershow";
    }

    @RequestMapping("offer/edit/{id}")
    public String edit(Locale locale, @PathVariable int id, Model model) {
        Offer offer = offerService.getOfferById(id).get();
        model.addAttribute("offer", offer);
        return "offerform";
    }

    @RequestMapping("/offer/delete/{id}")
    public String delete(Locale locale, @PathVariable int id) {
        offerService.deleteOffer(id);
        return "redirect:/offer";
    }

    @RequestMapping(value = {"/showCustomerOffers"}, method = RequestMethod.GET)
    public String showCustomerOffers(Authentication authentication, Locale locale, Model model) {

        Map<String, String> params = new HashMap<>();
        params.put("email", authentication.getName());
        User user = userService.getUserByEmail(authentication.getName());
        List<Offer> customerOffersList = offerService.getOffersByCustomerId((int)user.getId());

        customerOffersList = customerOffersList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        model.addAttribute("offers", customerOffersList);
        return "offerlistcustomer";
    }
}