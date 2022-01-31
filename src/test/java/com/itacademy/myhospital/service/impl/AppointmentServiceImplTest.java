package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.AppointmentRepository;
import com.itacademy.myhospital.model.repository.PersonRepository;
import com.itacademy.myhospital.model.repository.UserRepository;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static com.itacademy.myhospital.constants.Constants.*;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class AppointmentServiceImplTest {

    @Autowired
    private AppointmentService appointmentService;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AppointmentRepository appointmentRepository;
    @MockBean
    private EmailService emailService;
    Person person1;
    Person person2;
    private User user1;
    private User user2;
    private Role role1;
    private Role role2;
    Set<Role> roles1;
    Set<Role> roles2;
    List<Person> personList;
    private Appointment appointment;

    @BeforeEach
    public void setUp() {
        role1 = Role.builder()
                .id(1)
                .name("ROLE_DOCTOR")
                .build();
        role2 = Role.builder()
                .id(4)
                .name("ROLE_PATIENT")
                .build();
        roles1 = new HashSet<>();
        ;
        roles1.add(role1);
        roles1.add(role2);
        roles2 = new HashSet<>();
        roles2.add(role2);
        personList = new ArrayList<>();
        user1 = User.builder()
                .id(1)
                .username("myachin")
                .password("myachin")
                .email("Myachin@mail.ru")
                .verificationStatus(true)
                .verificationCode(null)
                .authenticationStatus(true)
                .roles(roles1)
                .build();
        user2 = User.builder()
                .id(1)
                .username("asdadw")
                .password("asdadaw")
                .email("Adsdn@mail.ru")
                .verificationStatus(true)
                .verificationCode(null)
                .authenticationStatus(true)
                .roles(roles2)
                .build();
        person1 = Person.builder()
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
        person2 = Person.builder()
                .id(2)
                .firstName("Beliy")
                .surname("Dima")
                .patronymic("Dimovich")
                .phoneNumber("297343238")
                .address("g.Minsk")
                .keyForUser("aksmdkq")
                .dateOfBirthday(LocalDate.now())
                .user(user2)
                .build();
        personList.add(person1);
        personList.add(person2);
        appointment = Appointment.builder()
                .id(1)
                .personal(person1)
                .isEngaged(false)
                .dateOfAppointment(Timestamp.valueOf(LocalDateTime.now().plusDays(2)))
                .build();
    }

    @Test
    void createListOfDays() {
        var listOfDays = appointmentService.createListOfDays(LocalDate.of(2022,1,12));
        assertEquals(6, listOfDays.size());
    }


    @Test
    void changeAppointmentOnBlockedValueTest() throws AppointmentException, MessagingException, UnsupportedEncodingException {

        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        appointmentService.changeAppointmentOnBlockedValue(person1.getPhoneNumber(),
                user1.getUsername(),
                1);
        assertTrue(appointment.isEngaged());
        assertEquals(person1.getPhoneNumber(), appointment.getPhoneNumber());
        assertEquals(user1, appointment.getUserPatient());
        verify(appointmentRepository, times(1)).findById(1);
        verify(appointmentRepository, times(1)).saveAndFlush(appointment);
        verify(emailService,times(1)).sendEmailAboutMakeAppointment(appointment,user1);

    }

    @Test
    void changeAppointmentOnBlockedValueFailTest() {
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(appointmentRepository.findById(2)).thenReturn(Optional.empty());
        Exception exception = assertThrows(AppointmentException.class,
                () -> appointmentService.changeAppointmentOnBlockedValue(person2.getPhoneNumber(),
                       user2.getUsername(),
                        2));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(2);
        verify(userRepository,times(1)).findUserByUsername(user2.getUsername());
    }
    @Test
    void changeAppointmentOnBlockedValueFailTest2() {
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(appointmentRepository.findById(2)).thenReturn(Optional.empty());
        Exception exception = assertThrows(AppointmentException.class,
                () -> appointmentService.changeAppointmentOnBlockedValue(person2.getPhoneNumber(),
                        user2.getUsername(),
                        2));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(2);
        verify(userRepository,times(1)).findUserByUsername(user2.getUsername());
    }
    @Test
    void changeAppointmentOnBlockedValueFailTest3() {
        appointment.setDateOfAppointment(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(appointmentRepository.findById(2)).thenReturn(Optional.of(appointment));
        Exception exception = assertThrows(AppointmentException.class,
                () -> appointmentService.changeAppointmentOnBlockedValue(person2.getPhoneNumber(),
                        user2.getUsername(),
                        2));
        assertTrue(exception.getMessage().contains(APPOINTMENT_IS_ALREADY_BLOCKED_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(2);
        verify(userRepository,times(1)).findUserByUsername(user2.getUsername());
    }

    @Test
    void cancelAppointmentByUserTest() throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException {
        appointment.setPhoneNumber(person2.getPhoneNumber());
        appointment.setEngaged(true);
        appointment.setUserPatient(user2);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        appointmentService.cancelAppointmentByUser(user2.getUsername(),1);
        assertFalse(appointment.isEngaged());
        assertNull(appointment.getUserPatient());
        assertNull(appointment.getPhoneNumber());
        verify(appointmentRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findUserByUsername(user2.getUsername());
        verify(emailService,times(1)).sendEmailAboutCanceledAppointmentByUser(appointment);
        verify(appointmentRepository,times(1)).saveAndFlush(appointment);
    }
    @Test
    void cancelAppointmentByUserFailTest1(){
        appointment.setPhoneNumber(person2.getPhoneNumber());
        appointment.setEngaged(true);
        appointment.setUserPatient(user2);
        when(appointmentRepository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.cancelAppointmentByUser(user2.getUsername(),2));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
    }
    @Test
    void cancelAppointmentByUserFailTest2(){
        appointment.setPhoneNumber(person2.getPhoneNumber());
        appointment.setEngaged(true);
        appointment.setUserPatient(user2);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(null);

        Exception exception = assertThrows(UserException.class,
                ()->appointmentService.cancelAppointmentByUser(user2.getUsername(),1));
        assertTrue(exception.getMessage().contains(NO_USER_WITH_USERNAME_EXCEPTION));
    }

    @Test
    void cancelAppointmentByDoctorTest() throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException {
        appointment.setPhoneNumber(person1.getPhoneNumber());
        appointment.setEngaged(true);
        appointment.setUserPatient(user2);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        when(personRepository.findByUser(user1)).thenReturn(person1);
        appointmentService.cancelAppointmentByDoctor(1,user1.getUsername());
        assertFalse(appointment.isEngaged());
        assertNull(appointment.getUserPatient());
        assertNull(appointment.getPhoneNumber());
        verify(appointmentRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findUserByUsername(user1.getUsername());
        verify(personRepository,times(1)).findByUser(person1.getUser());
        verify(emailService,times(1)).sendEmailAboutCanceledAppointmentByDoctor(appointment,person1);
        verify(appointmentRepository,times(1)).saveAndFlush(appointment);
    }
    @Test
    void cancelAppointmentByDoctorFailTest1(){
        when(appointmentRepository.findById(2)).thenReturn(Optional.empty());

        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.cancelAppointmentByDoctor(2,user2.getUsername()));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(2);
    }
    @Test
    void cancelAppointmentByDoctorFailTest2(){
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername("dadada")).thenReturn(null);
        Exception exception = assertThrows(UserException.class,
                ()->appointmentService.cancelAppointmentByDoctor(1,"dadada"));
        assertTrue(exception.getMessage().contains(USER_WITHOUT_A_PERSON_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findUserByUsername("dadada");
    }
    @Test
    void cancelAppointmentByDoctorFailTest3(){
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(personRepository.findByUser(user2)).thenReturn(null);
        Exception exception = assertThrows(UserException.class,
                ()->appointmentService.cancelAppointmentByDoctor(1,user2.getUsername()));
        assertTrue(exception.getMessage().contains(USER_WITHOUT_A_PERSON_EXCEPTION));
        verify(appointmentRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findUserByUsername(user2.getUsername());
        verify(personRepository,times(1)).findByUser(user2);
    }
    @Test
    void cancelAppointmentByDoctorFailTest4(){
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(personRepository.findByUser(user2)).thenReturn(person2);
        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.cancelAppointmentByDoctor(1,user2.getUsername()));
        assertTrue(exception.getMessage().contains(USER_HAS_NO_PERMISSIONS));
        verify(appointmentRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findUserByUsername(user2.getUsername());
        verify(personRepository,times(1)).findByUser(user2);
    }

    @Test
    void blockAppointmentByDoctorTest() throws AppointmentException {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(person1);
        var isTestCompleted = appointmentService.blockAppointmentByDoctor(1,user1.getUsername());
        assertTrue(isTestCompleted);
        assertEquals(person1,appointment.getPersonal());
        assertTrue(appointment.isEngaged());
        verify(appointmentRepository,times(1)).findById(1);
        verify(personRepository,times(1)).findPersonByUsernameOfUser(user1.getUsername());
        verify(appointmentRepository,times(1)).saveAndFlush(appointment);
    }
    @Test
    void blockAppointmentByDoctorFailTest(){
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(person1);
        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.blockAppointmentByDoctor(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
        verify(appointmentRepository,times(1)).findById(1);
    }
    @Test
    void blockAppointmentByDoctorFailTes2t(){
        appointment.setPersonal(person1);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(null);
        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.blockAppointmentByDoctor(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(USER_HAS_NO_PERMISSIONS));
        verify(appointmentRepository,times(1)).findById(1);
    }
    @Test
    void unblockAppointmentByDoctorTest() throws AppointmentException {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(person1);
        var isTestCompleted = appointmentService.unblockAppointmentByDoctor(1,user1.getUsername());
        assertTrue(isTestCompleted);
        assertFalse(appointment.isEngaged());
        verify(appointmentRepository,times(1)).findById(1);
        verify(personRepository,times(1)).findPersonByUsernameOfUser(user1.getUsername());
        verify(appointmentRepository,times(1)).saveAndFlush(appointment);
    }
    @Test
    void unblockAppointmentByDoctorFailTest(){
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(person1);
        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.unblockAppointmentByDoctor(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(NO_APPOINTMENT_EXCEPTION));
        verify(appointmentRepository,times(1)).findById(1);
    }
    @Test
    void unblockAppointmentByDoctorFailTest2(){
        appointment.setPersonal(person1);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(null);
        Exception exception = assertThrows(AppointmentException.class,
                ()->appointmentService.unblockAppointmentByDoctor(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(USER_HAS_NO_PERMISSIONS));
        verify(appointmentRepository,times(1)).findById(1);
    }
}