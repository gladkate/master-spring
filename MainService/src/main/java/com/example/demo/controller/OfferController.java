package com.example.demo.controller;

import com.offer.service.core.commands.OfferForm;
import com.offer.service.core.model.Offer;
import com.project.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@Controller
public class OfferController {

    private String uri = "http://localhost:7777/project/v1/offer/";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/addOffer", method = RequestMethod.GET)
    public ModelAndView showRegistrationPage( Locale locale, ModelAndView modelAndView, OfferForm offer) {
        modelAndView.addObject("offer", offer);
        modelAndView.setViewName("offerform");
        return modelAndView;
    }

    @RequestMapping(value = "/addOffer", method = RequestMethod.POST)
    public String processRegistrationForm(Authentication authentication, Locale locale, ModelAndView modelAndView, @Valid OfferForm offer, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "offerform";
        }

        Map<String, String> params = new HashMap<>();
        params.put("email", authentication.getName());
        User user= null;
        user = restTemplate.getForObject("http://localhost:9999/project/v1/user/getUserByEmail/"  + authentication.getName(), User.class, params);
        String url = uri + "addOffer";
        offer.setCustomer_id(String.valueOf(user.getId()));

        ResponseEntity<Offer> entity = restTemplate.postForEntity(url, offer, Offer.class);
        return "redirect:/offer/show/" + entity.getBody().getId();
    }


    @RequestMapping(value = {"/offer/list", "/offer"}, method = RequestMethod.GET)
    public String listProducts(Locale locale, Model model) {
        String url = uri + "getAllOffers";
        ResponseEntity<List<Offer>> rateResponse =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Offer>>() {
                });
        List<Offer> offersList = (ArrayList<Offer>) rateResponse.getBody();

        offersList = offersList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        model.addAttribute("offers", offersList);
        return "offerlist";
    }

    @RequestMapping("/offer/show/{id}")
    public String getProduct(Locale locale, @PathVariable String id, Model model) {

        String url = uri + "show/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(id, headers);
        ResponseEntity<Offer> offer = restTemplate.exchange(url, HttpMethod.GET, entity, Offer.class, id);
        model.addAttribute("offer", offer.getBody());
        return "offershow";
    }

    @RequestMapping("offer/edit/{id}")
    public String edit(Locale locale, @PathVariable String id, Model model) {
        String url = uri + "edit/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(id, headers);
        ResponseEntity<OfferForm> offer = restTemplate.exchange(url, HttpMethod.GET, entity, OfferForm.class, id);
        model.addAttribute("offer", offer.getBody());
        return "offerform";
    }

    @RequestMapping("/offer/delete/{id}")
    public String delete(Locale locale, @PathVariable String id) {
        String url = uri + "delete/{id}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(id, headers);
        ResponseEntity<String> offer = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class, id);
        return "redirect:/offer";
    }

    @RequestMapping(value = {"/showCustomerOffers"}, method = RequestMethod.GET)
    public String showCustomerOffers(Authentication authentication, Locale locale, Model model) {

        Map<String, String> params = new HashMap<>();
        params.put("email", authentication.getName());
        User user= null;
        user = restTemplate.getForObject("http://localhost:9999/project/v1/user/getUserByEmail/"  + authentication.getName(), User.class, params);

        String url = uri + "getOffersCreatedBy/{customer_id}";
        ResponseEntity<List<Offer>> rateResponse =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Offer>>() {
                }, String.valueOf(user.getId()));
        List<Offer> offersList = (ArrayList<Offer>) rateResponse.getBody();

        offersList = offersList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        model.addAttribute("offers", offersList);
        return "offerlistcustomer";
    }
}