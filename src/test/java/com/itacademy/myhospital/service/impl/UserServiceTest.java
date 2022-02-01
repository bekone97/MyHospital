package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.UserRepository;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.EmailService;
import com.itacademy.myhospital.service.UUIDService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static com.itacademy.myhospital.constants.Constants.LOCALHOST;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @MockBean
    private EmailService emailService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private UUIDService uuidService;

    private User user1;
    private User user2;

    private UserDto userDto;



    @BeforeEach
    public void setUp() {

        user1 = User.builder()
                .id(1)
                .username("Myachin")
                .password("Myachin")
                .email("Myachin@mail.ru")
                .verificationStatus(true)
                .verificationCode(null)
                .authenticationStatus(true)
                .img("img/img1")
                .build();
        user2 = User.builder()
                .id(2)
                .username("Dima")
                .password("Dima")
                .email("Dima@mail.ru")
                .verificationStatus(false)
                .verificationCode("daskdmqkwq")
                .authenticationStatus(false)
                .img("img/img2")
                .build();
        userDto = new UserDto();
        userDto.setUsername("bababa");
        userDto.setEmail("emkds@mail.ru");
        userDto.setPassword("aspdlap");

    }

    @Test
    void findByUsernameTest() {
        user1.setUsername("username");
        when(userRepository.findUserByUsername("username")).thenReturn(user1);
        var user = userRepository.findUserByUsername("username");
        verify(userRepository, times(1)).findUserByUsername("username");
        assertEquals("username", user.getUsername());
    }

    @Test
    void findByUsernameFailTest() {
        when(userRepository.findUserByUsername("username")).thenReturn(null);
        var user = userRepository.findUserByUsername("username");
        verify(userRepository, times(1)).findUserByUsername("username");
        assertNull(user);
    }

    @Test
    void createCodeAndSaveUserTest() throws UserException, MessagingException, UnsupportedEncodingException {
        var user1Dto = new UserDto();
        user1Dto.setUsername("bababa");
        user1Dto.setEmail("emkds@mail.ru");
        user1Dto.setPassword("aspdlap");
        String uuid = "5211e915-c3e2-4dcb-0776-c7b900f38ab7";
        when(userRepository.findUserByUsername("bababa")).thenReturn(null);
        when(uuidService.getRandomString()).thenReturn(uuid);
        when(userRepository.findByVerificationCode(uuid)).thenReturn(null);
        when(bCryptPasswordEncoder.encode("aspdlap")).thenReturn("dadada");
        var isUserSaved= userService.createCodeAndSaveUser(user1Dto);
        verify(userRepository, times(1)).findUserByUsername(user1Dto.getUsername());
        assertTrue(isUserSaved);
    }


    @Test
    void checkAndChangeVerificationStatusFailTest() {
        when(userRepository.findByVerificationCode("dadada")).thenReturn(null);
        boolean isChanged=userService.checkAndChangeVerificationStatus("dadada");
        verify(roleService, times(0)).findById(4);
        assertFalse(isChanged);
    }
    @Test
    void checkAndChangeVerificationStatusTest() {
        when(userRepository.findByVerificationCode(user2.getVerificationCode())).thenReturn(user2);
        boolean isChanged=userService.checkAndChangeVerificationStatus("daskdmqkwq");
        verify(roleService,times(1)).findById(4);
        assertTrue(isChanged);
        assertTrue(user2.getVerificationStatus());
        assertNull(user2.getVerificationCode());
        verify(userRepository,times(1)).saveAndFlush(user2);
    }

    @Test
    void saveUpdatedUser() throws MessagingException, UserException, IOException {
        var userDto1 = UserDto.builder()
                .id(2)
                .username("Dima")
                .password("Dima")
                .email("Dimochka@mail.ru")
                .verificationStatus(false)
                .authenticationStatus(false)
                .img("img/img3")
                .build();
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        when(userRepository.findById(userDto1.getId())).thenReturn(Optional.of(user2));
        userService.saveUpdatedUser(userDto1, file);
        verify(emailService,times(1)).sendEmailAboutChangeEmail(user2,LOCALHOST);
        verify(userRepository,times(1)).saveAndFlush(user2);
    }
    @Test
     void checkChangeOfEmailTest() throws MessagingException, UnsupportedEncodingException {
        var userDto1 = UserDto.builder()
                .id(1)
                .username("Myachin")
                .password("Myachin")
                .email("Myachin1997@mail.ru")
                .verificationStatus(true)
                .build();
        boolean isChanged=userService.checkChangeOfEmail(userDto1,user1);
        assertFalse(user1.getVerificationStatus());
        assertNull(user1.getRoles());
        assertEquals(userDto1.getEmail(),user1.getEmail());
        assertTrue(isChanged);
        verify(emailService,times(1)).sendEmailAboutChangeEmail(user1,LOCALHOST);
    }
    @Test
    void checkChangeOfEmailFailTest() throws MessagingException, UnsupportedEncodingException {
        var userDto1 = UserDto.builder()
                .id(1)
                .username("Myachin")
                .password("Myachin")
                .email("Myachin@mail.ru")
                .verificationStatus(true)
                .build();
        boolean isChanged=userService.checkChangeOfEmail(userDto1,user1);
        assertFalse(isChanged);
    }
}