package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class MainControllerTest {
    @Autowired
    private WebApplicationContext context;


    private MockMvc mockMvc;

    @MockBean
    UserController userController;
    @MockBean
    private Principal principal;

    @MockBean
    private UserService userService;


    private User user;
    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        user= User.builder()
                .id(1)
                .username("user")
                .password("password")
                .email("User@mail.ru")
                .build();


    }

    @Test
    @WithMockUser(username = "user",roles = ("PATIENT"))
    void mainPageWithUser() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(xpath("//*[@id='nameOfUser']").string("user"))
                .andExpect(xpath("//*[@id='navBarTable']/div").nodeCount(6))
                .andExpect(view().name("home-page"));
    }
    @Test
    void mainPageWithoutUser() throws Exception {
        when(principal.getName()).thenReturn(null);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(0))
                .andExpect(xpath("//*[@id='nonUser']").string("Account"))
                .andExpect(xpath("//*[@id='navBarTable']/div").nodeCount(4))
                .andExpect(view().name("home-page"));;
    }

    @Test
    @WithMockUser(username = "user",roles = {"NURSE","PATIENT"})
    void mainPageWithNurse() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(xpath("//*[@id='nameOfUser']").string("user"))
                .andExpect(xpath("//*[@id='navBarTable']/div").nodeCount(7))
                .andExpect(view().name("home-page"));;
    }
    @Test
    @WithMockUser(username = "user",roles = {"NURSE","PATIENT","DOCTOR"})
    void mainPageWithDoctor() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(xpath("//*[@id='nameOfUser']").string("user"))
                .andExpect(xpath("//*[@id='navBarTable']/div").nodeCount(7))
                .andExpect(view().name("home-page"));
    }
    @Test
    @WithMockUser(username = "user",roles = {"NURSE","PATIENT","DOCTOR","ADMIN"})
    void mainPageWithAdmin() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(userService.findByUsername("user")).thenReturn(user);
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(xpath("//*[@id='nameOfUser']").string("user"))
                .andExpect(xpath("//*[@id='navBarTable']/div").nodeCount(8))
                .andExpect(view().name("home-page"));
    }
}