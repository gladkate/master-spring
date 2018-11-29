package com.example.demo.service;

import com.example.demo.model.Offer;
import com.example.demo.repository.OfferDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OfferService {

     @Autowired
     private OfferDao offerDao;

     public List<Offer> getOffersByCustomerId(long customer_id) {
//          List<Offer> offers = new ArrayList<>();
          List<Offer> offers = (List<Offer> ) offerDao.findAll();
          offers = offers.stream().filter(o ->(String.valueOf(o.getCustomer_id()).equals(String.valueOf(customer_id)))).collect(Collectors.toList());
          return (List<Offer> ) offers;
     }

     public List<Offer> getAllOffers() {
          return (List<Offer> ) offerDao.findAll();
     }

     public Optional<Offer> getOfferById(int id){
          return offerDao.findById(id);
     }

     public Offer createOffer(Offer offer){
          offerDao.save(offer);
          return offer;
     }

     public void deleteOffer(int id) {
          offerDao.deleteOfferById(id);
     }
     public Offer saveOrUpdateOffer(Offer offer) {
          Offer savedOffer = createOffer(offer);

          System.out.println("Saved Product Id: " + savedOffer.getId());
          return savedOffer;
     }
}
