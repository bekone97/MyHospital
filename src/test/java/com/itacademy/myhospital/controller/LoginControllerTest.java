package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.itacademy.myhospital.constants.Constants.ERROR_EXCEPTION_PAGE;
import static com.itacademy.myhospital.constants.Constants.LOGIN_PAGE;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class LoginControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserDto userDto1;
    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        userDto1=UserDto.builder()
                .username("user")
                .password("password")
                .email("User@mail.ru")
                .authenticationStatus(false)
                .build();
    }
    @Test
    void authorizationGetTest() throws Exception {
        var dto = new UserDto();
        this.mockMvc.perform(get("/authorization"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("user",dto))
                .andExpect(view().name("authorization"));
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void authorizationGetFailTest() throws Exception {
        this.mockMvc.perform(get("/authorization"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    void authorizationPostTest() throws Exception {
        when(userService.createCodeAndSaveUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/authorization")
                .flashAttr("user",userDto1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(userService,times(1)).createCodeAndSaveUser(userDto1);
    }

    @Test
    void authorizationPostWrongValidTest() throws Exception {
        userDto1.setEmail("adsq");
        userDto1.setUsername("as");
        userDto1.setPassword("as");
        this.mockMvc.perform(post("/authorization")
                        .flashAttr("user",userDto1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                        .andExpect(view().name("authorization"));
    }
    @Test
    void authorizationPostWrongFailTest1() throws Exception {
       when(userService.createCodeAndSaveUser(userDto1)).thenThrow(new UserException("User with this username also exist"));
        this.mockMvc.perform(post("/authorization")
                        .flashAttr("user",userDto1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("error","User with this username also exist"))
                .andExpect(model().attribute("user",userDto1))
                .andExpect(view().name("authorization"));
        verify(userService,times(1)).createCodeAndSaveUser(userDto1);
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void authorizationPostWrongFailTest2() throws Exception {
        this.mockMvc.perform(post("/authorization")
                        .flashAttr("user",userDto1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void makeVerificationTest() throws Exception {
        String code = "asldkasdlq";
        when(userService.checkAndChangeVerificationStatus(code)).thenReturn(true);
        this.mockMvc.perform(get("/verification")
                .param("code",code))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(userService,times(1)).checkAndChangeVerificationStatus(code);
    }
    @Test
    void makeVerificationWithoutUserTest() throws Exception {
        String code = "asldkasdlq";
        when(userService.checkAndChangeVerificationStatus(code)).thenReturn(true);
        this.mockMvc.perform(get("/verification")
                        .param("code",code))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_PAGE));
        verify(userService,times(0)).checkAndChangeVerificationStatus(code);
    }

    @Test
    void loginPageTest() throws Exception {
                this.mockMvc.perform(get("/login"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().size(0))
                        .andExpect(view().name("login"));
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void loginPageTest2() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
                    }
}