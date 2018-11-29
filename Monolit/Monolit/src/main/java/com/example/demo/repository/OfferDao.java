package com.example.demo.repository;

import com.example.demo.model.Offer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface OfferDao extends CrudRepository<Offer, Integer> {

    void deleteOfferById(int id);

}
