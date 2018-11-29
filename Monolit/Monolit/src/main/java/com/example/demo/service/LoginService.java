package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public boolean validateUser(String email, String password) {

        User user = userDao.findByEmail(email);
        if ( user != null && bCryptPasswordEncoder.matches(password,user.getPassword())) {
            return true;
        } else
            return false;
    }

}