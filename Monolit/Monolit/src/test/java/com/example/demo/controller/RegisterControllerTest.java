package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserDao;
import com.example.demo.service.EmailService;

import com.example.demo.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.configuration.Messages;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RegisterControllerTest {

    @InjectMocks
    private RegisterController registerController;
    @Mock
    private BindingResult bindingResult;
    @Spy
    private UserService userService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private EmailService emailService;
    @Mock
    private Messages messages;

    @Spy
    private UserDao userDao;

    @Test
    public void testProcesRegistrationFormWhenUserDoesNotExist() {
        //given

        //when
        ModelAndView modelAndView = registerController.processRegistrationForm(null, new ModelAndView(), new User(),
                 httpServletRequest);
        //then
        assertThat(modelAndView.getViewName().equals("register"));
        verify(emailService).sendEmail(any());
    }

    @Test
    public void testProcesRegistrationFormWhenUserExists() {
        //given
        User user = new User();
        user.setEmail("email@wp.pl");
        when(bindingResult.hasErrors()).thenReturn(true);
        //when
        ModelAndView modelAndView = registerController.processRegistrationForm(null, new ModelAndView(), new User(),
                 httpServletRequest);
        //then
        assertThat(modelAndView.getViewName().equals("register"));
//        verify(emailService).sendEmail(any());
    }
}
