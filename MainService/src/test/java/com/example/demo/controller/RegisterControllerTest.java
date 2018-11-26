package com.example.demo.controller;

import com.example.demo.service.EmailService;
import com.project.core.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import com.example.demo.configuration.Messages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RegisterControllerTest {

    @InjectMocks
    private RegisterController registerController;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private EmailService emailService;
    @Mock
    private Messages messages;

    @Test
    public void shouldRegisterUserWhenUserDoesNotExist() {
        //given
        when(restTemplate.getForObject(anyString(), any(), anyMap())).thenReturn(new User());
        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(new ResponseEntity<>(new User(), HttpStatus.OK));
        //when
        ModelAndView modelAndView = registerController.processRegistrationForm(null,
                new ModelAndView(), new User(), new MockHttpServletRequest());
        //then
        assertThat(modelAndView.getViewName().equals("register"));
        assertThat(modelAndView.getModelMap().get("sent").equals(true));
        verify(restTemplate).getForObject(anyString(), any(), anyMap());
        verify(emailService).sendConfirmationEmail(any(),anyString(), any());
    }


    @Test
    public void shouldNotRegisterWhenUserExists() {
        //given
        User user = new User();
        user.setEmail("email@wp.pl");
        when(restTemplate.getForObject(anyString(), any(), anyMap())).thenReturn(user);
        //when
        ModelAndView modelAndView = registerController.processRegistrationForm(null,
                new ModelAndView(), new User(), new MockHttpServletRequest());
        //then
        assertThat(modelAndView.getViewName().equals("register"));
        assertThat(modelAndView.getModelMap().get("sent").equals(false));
        verify(restTemplate).getForObject(anyString(), any(), anyMap());
    }
}
