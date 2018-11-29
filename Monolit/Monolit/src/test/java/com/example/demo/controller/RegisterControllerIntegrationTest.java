package com.example.demo.controller;

import com.example.demo.DemoApplication;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@TestPropertySource(locations ="classpath:application-test.properties")
public class RegisterControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests{

    @Autowired
    private RegisterController registerController;

    @Autowired
    private UserService userService;

    @Test
    public void shouldRegisterUserWhenUserDoesNotExist() {
        //given
        User user = new User();
        user.setEmail(UUID.randomUUID().toString()+ "@com.com");
        user.setLastName("Kowalski");
        user.setFirstName("Jan");
        //when
        ModelAndView modelAndView = registerController.processRegistrationForm(null,
                new ModelAndView(), user, new MockHttpServletRequest());
        //then
        assertThat(modelAndView.getViewName().equals("register"));
        assertThat(modelAndView.getModelMap().get("sent").equals(true));
    }

    @Test
    public void shouldNotRegisterWhenUserExists() {
        //given
        User user = new User();
        user.setEmail(UUID.randomUUID().toString()+ "@com.com");
        user.setLastName("Kowalski");
        user.setFirstName("Jan");
        userService.saveUser(user);
        //when
       ModelAndView modelAndView = registerController.processRegistrationForm(null,
               new ModelAndView(), user, new MockHttpServletRequest());
        //then
        assertThat(modelAndView.getViewName().equals("register"));
        assertThat(modelAndView.getModelMap().get("sent")).isEqualTo(false);
    }

    private void cleanup(String email) {
        User user = userService.getUserByEmail(email);
        userService.delete(user);
    }




    @Resource
    private WebApplicationContext context;

    private MockMvc mockMvc;



    @Before
    public void beforeSetupMock() {
        mockMvc  = MockMvcBuilders.webAppContextSetup(context).build();
    }

//    @Test
//    public void test() throws Exception {
//        mockMvc.perform(get("/home"))
//                .andExpect(status().isOk());
//    }
}
