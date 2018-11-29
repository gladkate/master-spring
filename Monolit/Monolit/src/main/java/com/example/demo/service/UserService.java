package com.example.demo.service;


import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleDao;
import com.example.demo.repository.UserDao;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    public void registerUserAndSendEmail(User user, String appUrl, Locale locale){
        saveUser(user);
        SimpleMailMessage registrationEmail = emailService.registrationConfirmationEmailMessate(user, appUrl, locale);
        emailService.sendEmail(registrationEmail);
    }

    public void saveUser(User user) {
        user.setEnabled(false);
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setActive(1);
        Role userRole = roleDao.findByRole("SIMPLE_USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        userDao.save(user);
    }

    public void updateUser(User user) {
        if (user != null) {
            userDao.save(user);
        }
    }

    public User getUserByEmail(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            user = new User();
        }
        Role userRole = roleDao.findByRole("SIMPLE_USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return user;
    }

    public User getUserByConfirmationToken(String confirmationToken) {
        User user = userDao.findByConfirmationToken(confirmationToken);
        if (user == null) {
            user = new User();
        }
        Role userRole = roleDao.findByRole("SIMPLE_USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return user;
    }

    public List<User> getAllUsers() {
        List<User> listOfUsers = (List<User>)userDao.findAll();
        return listOfUsers;
    }

    public void delete(User user){
        userDao.delete(user);
    }

}
