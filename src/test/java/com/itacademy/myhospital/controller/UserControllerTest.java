package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private Principal principal;

    @MockBean
    private UserService userService;

    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private RoleService roleService;

    @MockBean
    private PersonService personService;
    private User user1;
    private User user2;
    private Person person;
    private Role role1;
    private Role role2;
    private List<User> users;
    private Page<User> usersPage;
    private UserDto userDto1;
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        role1=Role.builder()
                .id(1)
                .name("PATIENT")
                .build();
        role2=Role.builder()
                .id(2)
                .name("NURSE")
                .build();
        user1 = User.builder()
                .id(1)
                .username("user")
                .password("password")
                .email("User@mail.ru")
                .img("asldaldk")
                .build();
        userDto1=UserDto.builder()
                .id(user1.getId())
                .username(user1.getUsername())
                .password(user1.getPassword())
                .img(user1.getImg())
                .email(user1.getEmail())
                .authenticationStatus(user1.getAuthenticationStatus())
                .build();
        user2 = User.builder()
                .id(1)
                .username("user")
                .password("password")
                .email("User@mail.ru")
                .build();
        users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        person = Person.builder()
                .id(1)
                .firstName("Myachin")
                .surname("Artem")
                .patronymic("Valerevich")
                .phoneNumber("297342938")
                .address("g.Grodno")
                .keyForUser("asl,qlw,q")
                .dateOfBirthday(LocalDate.now())
                .user(user1)
                .build();
        Pageable pageable = PageRequest.of(1, 2);
        usersPage = new PageImpl<>(users, pageable, 2);
    }

    @Test
    @WithMockUser(username = "user",roles = "ADMIN")
    void userListByPageTest() throws Exception {
    when(userService.findAll(1,"asc","id")).thenReturn(usersPage);
    this.mockMvc.perform(get("/users/1").param("sortField","asc").param("sortDirection","id"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().size(5))
            .andExpect(model().attribute("page",usersPage))
            .andExpect(model().attribute("users",users))
            .andExpect(xpath("//*[@id='bodyOfUsers']/div").nodeCount(2))
            .andExpect(view().name("user/users"));
    verify(userService,times(1)).findAll(1,"asc","id");
    }
    @Test
    void userListByPageWithoutUserTest() throws Exception {
        when(userService.findAll(1,"asc","id")).thenReturn(usersPage);
        this.mockMvc.perform(get("/users/1").param("sortField","asc").param("sortDirection","id"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void userListByPageUserDoctorTest() throws Exception {
        when(userService.findAll(1,"asc","id")).thenReturn(usersPage);
        this.mockMvc.perform(get("/users/1").param("sortField","asc").param("sortDirection","id"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user",roles = ("PATIENT"))
    void userInfoWithUser() throws Exception {
        userDto1.setAuthenticationStatus(false);
        when(principal.getName()).thenReturn(user1.getUsername());
        when(userService.getDtoByUsernameForProfile(user1.getUsername())).thenReturn(userDto1);
        this.mockMvc.perform(get("/userProfile"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("user",userDto1))
                .andExpect(xpath("//*[@id='userTextInformation']/div").nodeCount(2));
        verify(userService,times(1)).getDtoByUsernameForProfile(user1.getUsername());
    }
    @Test
    void userInfoWithoutUser() throws Exception {
        this.mockMvc.perform(get("/userProfile"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void userByIdTest() throws Exception {
        when(userService.getDtoById(1)).thenReturn(userDto1);
        this.mockMvc.perform(get("/user/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(model().attribute("user",userDto1))
                .andExpect(view().name("user/user-info"));
        verify(userService,times(1)).getDtoById(1);
    }
    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void userByIdFailTest1() throws Exception {
        when(userService.getDtoById(20)).thenThrow(UserException.class);
        this.mockMvc.perform(get("/user/20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(userService,times(1)).getDtoById(20);
    }
    @Test
    @WithMockUser(username = "user",roles = ("DOCTOR"))
    void userByIdFailTest2() throws Exception {
        this.mockMvc.perform(get("/user/20"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void userByIdWithoutUserFailTest3() throws Exception {
        this.mockMvc.perform(get("/user/20"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user",roles = ("PATIENT"))
    void userSettingsForUserTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(userService.getDtoByUsernameForSettings(user1.getUsername())).thenReturn(userDto1);
        this.mockMvc.perform(get("/userSettings"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(model().attribute("user",userDto1))
                .andExpect(view().name("user/edit-user"));
        verify(userService,times(1)).getDtoByUsernameForSettings(user1.getUsername());
    }
    @Test
    void userSettingsWithoutUser() throws Exception {
        this.mockMvc.perform(get("/userSettings"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user",roles = "ADMIN")
    void userSettingsForAdminTest() throws Exception {
        List<Role> listOfRoles= new ArrayList<>();
        listOfRoles.add(role1);
        listOfRoles.add(role2);
        when(userService.getDtoByIdForSettings(1)).thenReturn(userDto1);
        when(roleService.findAll()).thenReturn(listOfRoles);
        this.mockMvc.perform(get("/userSettings/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("user",userDto1))
                .andExpect(model().attribute("roles",listOfRoles))
                .andExpect(view().name("user/edit-user-for-admin"));
        verify(userService,times(1)).getDtoByIdForSettings(1);
        verify(roleService,times(1)).findAll();
    }
    @Test
    void userSettingsForAdminFailTest1() throws Exception {
        this.mockMvc.perform(get("/userSettings/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void userSettingsForAdminFailTest2() throws Exception {
        when(userService.getDtoByIdForSettings(20)).thenThrow(UserException.class);
        this.mockMvc.perform(get("/userSettings/20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(userService,times(1)).getDtoByIdForSettings(20);
    }
    @Test
    @WithMockUser(username = "user",roles = ("DOCTOR"))
    void userSettingsForAdminFailTest3() throws Exception {
        this.mockMvc.perform(get("/userSettings/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void updateUserTest() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        this.mockMvc.perform(multipart("/updateUser").file(userImg)
                .param("username",userDto1.getUsername())
                .param("password",userDto1.getPassword())
                        .param("email",userDto1.getEmail())
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/userProfile"));
    }
    @Test
    @WithMockUser(username = "user",roles = "PATIENT")
    void updateUserWithWrongValidTest() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        this.mockMvc.perform(multipart("/updateUser").file(userImg)
                        .param("username","ka")
                        .param("password","ba")
                        .param("email",userDto1.getEmail())
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(2))
                .andExpect(view().name("user/edit-user"));
    }
    @Test
    void updateUserWithWrongWithoutUserTest() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        this.mockMvc.perform(multipart("/updateUser").file(userImg)
                        .param("username","ka")
                        .param("password","ba")
                        .param("email",userDto1.getEmail())
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void updateUserByAdminTest() throws Exception {
        when(userService.checkChangeOfRoles(userDto1)).thenReturn(true);
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        Set<Role> roles = new HashSet<>();
        roles.add(role2);
        roles.add(role1);
        roles.add(Role.builder()
                .id(3)
                .name("ROLE_DOCTOR")
                .build());
        userDto1.setRoles(roles);
        this.mockMvc.perform(multipart("/updateUserByAdmin").file(userImg)
                        .flashAttr("user",userDto1)
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/"+userDto1.getId()));
        verify(userService,times(1)).checkChangeOfRoles(userDto1);
        verify(appointmentService,times(1)).addAppointmentsForNewDoctorForWeek(userDto1.getUsername());
        verify(userService,times(1)).saveUpdatedUser(userDto1,userImg);
    }
    @Test
    @WithMockUser(username = "user",roles = ("DOCTOR"))
    void updateUserByAdminFailTest() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        this.mockMvc.perform(multipart("/updateUserByAdmin").file(userImg)
                        .param("id",userDto1.getId().toString())
                        .param("username",userDto1.getUsername())
                        .param("password",userDto1.getPassword())
                        .param("email",userDto1.getEmail())
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    void updateUserByAdminWithoutUser() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        this.mockMvc.perform(multipart("/updateUserByAdmin").file(userImg)
                        .param("id",userDto1.getId().toString())
                        .param("username",userDto1.getUsername())
                        .param("password",userDto1.getPassword())
                        .param("email",userDto1.getEmail())
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void updateUserByAdminWrongValid() throws Exception {
        MockMultipartFile userImg
                = new MockMultipartFile(
                "userImg",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        this.mockMvc.perform(multipart("/updateUserByAdmin").file(userImg)
                        .param("id",userDto1.getId().toString())
                        .param("username","ky")
                        .param("password","ky")
                        .param("email","sa")
                        .param("userImg","userImg"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(3))
                .andExpect(view().name("user/edit-user-for-admin"));
    }

    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void deleteUserTest() throws Exception {
        when(userService.deleteById(1)).thenReturn(true);
        this.mockMvc.perform(post("/deleteUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/1?sortField=id&sortDirection=asc"));
        verify(userService,times(1)).deleteById(1);
    }
    @Test
    @WithMockUser(username = "user",roles = ("ADMIN"))
    void deleteUserFailTest1() throws Exception {
        when(userService.deleteById(1)).thenThrow(UserException.class);
        this.mockMvc.perform(post("/deleteUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(userService,times(1)).deleteById(1);
    }
    @Test
    @WithMockUser(username = "user",roles = {"DOCTOR","NURSE","PATIENT"})
    void deleteUserFailTest2() throws Exception {
        when(userService.deleteById(1)).thenThrow(UserException.class);
        this.mockMvc.perform(post("/deleteUser/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        verify(userService,times(0)).deleteById(1);
    }

    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void userAuthenticationTest() throws Exception {
        this.mockMvc.perform(get("/userAuthentication"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("authentication-key"));
    }
    @Test
    void userAuthenticationFailTest() throws Exception {
        this.mockMvc.perform(get("/userAuthentication"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void makeAuthenticationTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(personService.addUserToPerson(person.getKeyForUser(),user1.getUsername())).thenReturn(person);
        this.mockMvc.perform(post("/authentication")
                .param("key",person.getKeyForUser()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/"+person.getId()));
        verify(personService,times(1)).addUserToPerson(person.getKeyForUser(),user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void makeAuthenticationFailTest1() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(personService.addUserToPerson(person.getKeyForUser(),user1.getUsername())).thenThrow(UserException.class);
        this.mockMvc.perform(post("/authentication")
                        .param("key",person.getKeyForUser()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/userProfile"));
        verify(personService,times(1)).addUserToPerson(person.getKeyForUser(),user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void makeAuthenticationFailTest2() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(personService.addUserToPerson(person.getKeyForUser(),user1.getUsername())).thenThrow(PersonException.class);
        this.mockMvc.perform(post("/authentication")
                        .param("key",person.getKeyForUser()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("error","Person also has a user or no person with this key"))
                .andExpect(view().name("authentication-key"));
        verify(personService,times(1)).addUserToPerson(person.getKeyForUser(),user1.getUsername());
    }
    @Test
    void makeAuthenticationFailTest3() throws Exception {
        this.mockMvc.perform(post("/authentication")
                        .param("key",person.getKeyForUser()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void updatePasswordOfUserTest() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUser")
                        .param("username",userDto1.getUsername())
                        .param("password",userDto1.getPassword())
                        .param("email",userDto1.getEmail())
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
    @Test
    @WithMockUser(username = "user",roles = {"PATIENT","NURSE","DOCTOR"})
    void updatePasswordOfUserFailTest1() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUser")
                        .param("username","kyk")
                        .param("password","ka")
                        .param("email","b")
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(view().name("user/edit-user"));
    }
    @Test
    void updatePasswordOfUserFailTest2() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUser")
                        .param("username","kyk")
                        .param("password","ka")
                        .param("email","b")
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
    @Test
    @WithMockUser(username = "user",roles = {"ADMIN"})
    void updatePasswordOfUserByAdminTest() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUserByAdmin")
                        .param("username",userDto1.getUsername())
                        .param("password",userDto1.getPassword())
                        .param("email",userDto1.getEmail())
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"+userDto1.getId()));
    }
    @Test
    @WithMockUser(username = "user",roles = {"ADMIN"})
    void updatePasswordOfUserByAdminFailTest1() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUserByAdmin")
                        .param("username","kyk")
                        .param("password","ka")
                        .param("email","b")
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(view().name("user/edit-user-for-admin"));
    }
    @Test
    @WithMockUser(username = "user",roles = {"DOCTOR"})
    void updatePasswordOfUserByAdminFailTest2() throws Exception {
        when(userService.updatePasswordOfUser(userDto1)).thenReturn(true);
        this.mockMvc.perform(post("/updatePasswordOfUserByAdmin")
                        .param("username","kyk")
                        .param("password","ka")
                        .param("email","b")
                        .param("id",userDto1.getId().toString())
                        .param("img",userDto1.getImg()))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }
}