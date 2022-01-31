package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.MedicalHistoryRepository;
import com.itacademy.myhospital.model.repository.PersonRepository;
import com.itacademy.myhospital.model.repository.UserRepository;
import com.itacademy.myhospital.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static com.itacademy.myhospital.constants.Constants.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import static com.itacademy.myhospital.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class MedicalHistoryServiceImplTest {
    @Autowired
    private MedicalHistoryService medicalHistoryService;
    @MockBean
    private MedicalHistoryRepository medicalHistoryRepository;
    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private UserRepository userRepository;


    private Role role1;
    private Role role2;
    private Set<Role> roles1;
    private Set<Role> roles2;
    private Person person1;
    private Person person2;
    private User user1;
    private Diagnosis diagnosis1;
    private MedicalHistoryProcess medicalHistoryProcess1;
    private MedicalHistoryProcess medicalHistoryProcess2;
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
    private List<NameOfProcess> nameOfProcesses;
    private NameOfProcess nameOfProcess1;
    private NameOfProcess nameOfProcess2;
    private MedicalHistory medicalHistory;
    private MedicalHistory medicalHistory2;
    private List<MedicalHistory> medicalHistories;
    private User user2;

    @BeforeEach
    public void setUp() {
        role1 = Role.builder()
                .id(1)
                .name("ROLE_NURSE")
                .build();
        role2 = Role.builder()
                .id(4)
                .name("ROLE_PATIENT")
                .build();
        roles1 = new HashSet<>();
        roles1.add(role1);
        roles1.add(role2);
        roles2=new HashSet<>();
        roles2.add(role2);
        nameOfProcess1= NameOfProcess.builder()
                .id(1)
                .name("Urinoteropia")
                .process(Process.builder()
                        .id(1)
                        .name("PROCEDURE")
                        .build())
                .build();
        nameOfProcess2= NameOfProcess.builder()
                .id(2)
                .name("Zhirootsos")
                .process(Process.builder()
                        .id(1)
                        .name("OPERATION")
                        .build())
                .build();
        nameOfProcesses=new ArrayList<>();
        nameOfProcesses.add(nameOfProcess1);
        nameOfProcesses.add(nameOfProcess2);
        medicalHistoryProcess1= MedicalHistoryProcess.builder()
                .id(1)
                .medicalHistory(medicalHistory)
                .nameOfProcess(nameOfProcess1)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcess2= MedicalHistoryProcess.builder()
                .id(2)
                .medicalHistory(medicalHistory)
                .nameOfProcess(nameOfProcess2)
                .numberOfDays(1)
                .quantityPerDay(1)
                .status(false)
                .build();
        medicalHistoryProcesses=new ArrayList<>();
        medicalHistoryProcesses.add(medicalHistoryProcess1);
        medicalHistoryProcesses.add(medicalHistoryProcess2);
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
                .username("myachin")
                .password("myachin")
                .email("Myachin@mail.ru")
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
                .user(null)
                .build();
       diagnosis1= Diagnosis.builder()
                .id(1)
                .name("ORVI")
                .personal(person1)
                .build();
        medicalHistory =MedicalHistory.builder()
                .id(1)
                .dischargeStatus(false)
                .diagnosis(diagnosis1)
                .complain("Dadadada")
                .patient(person2)
                .receiptDate(Timestamp.valueOf(LocalDateTime.now()))
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
        medicalHistory2 =MedicalHistory.builder()
                .id(2)
                .dischargeStatus(false)
                .diagnosis(diagnosis1)
                .complain("Dadadada")
                .patient(person2)
                .receiptDate(Timestamp.valueOf(LocalDateTime.now()))
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
        medicalHistories=new ArrayList<>();
        medicalHistories.add(medicalHistory);
        medicalHistories.add(medicalHistory2);
    }

    @Test
    void findByIdTest() throws MedicalHistoryException {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        var maybeMedicalHistory = medicalHistoryService.findById(1);
        assertEquals(medicalHistory,maybeMedicalHistory);
        verify(medicalHistoryRepository,times(1)).findById(1);
    }
    @Test
    void findByIdFailTest(){
        when(medicalHistoryRepository.findById(3)).thenReturn(Optional.empty());
        Exception exception = assertThrows(MedicalHistoryException.class,
                ()->medicalHistoryService.findById(3));
        assertTrue(exception.getMessage().contains(NO_MEDICAL_HISTORY_EXCEPTION));
        verify(medicalHistoryRepository,times(1)).findById(3);
    }

    @Test
    void deleteByIdFailTest() {
        when(medicalHistoryRepository.existsById(3)).thenReturn(false);
        Exception exception = assertThrows(MedicalHistoryException.class,
                ()->medicalHistoryService.deleteById(3));
        assertTrue(exception.getMessage().contains(NO_MEDICAL_HISTORY_EXCEPTION));
        verify(medicalHistoryRepository,times(1)).existsById(3);
    }

    @Test
    void dischargePatientTest() throws MedicalHistoryException {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        medicalHistoryService.dischargePatient(1);
        assertTrue(medicalHistory.isDischargeStatus());
        assertNotNull(medicalHistory.getDischargeDate());
        for (MedicalHistoryProcess process:
             medicalHistory.getMedicalHistoryProcesses()) {
            assertTrue(process.isStatus());
        }
        verify(medicalHistoryRepository,times(1)).findById(1);
        verify(medicalHistoryRepository,times(1)).saveAndFlush(medicalHistory);
    }
    @Test
    void dischargePatientFailTest1() {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.empty());
        Exception exception = assertThrows(MedicalHistoryException.class,
                ()->medicalHistoryService.dischargePatient(1));
        assertTrue(exception.getMessage().contains(NO_MEDICAL_HISTORY_EXCEPTION));
    }
    @Test
    void dischargePatientFailTest2() {
        medicalHistory.setDischargeStatus(true);
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        Exception exception = assertThrows(MedicalHistoryException.class,
                ()->medicalHistoryService.dischargePatient(1));
        assertTrue(exception.getMessage().contains(MEDICAL_HISTORY_HAS_ALREADY_DISCHARGED_EXCEPTION));
        verify(medicalHistoryRepository,(times(1))).findById(1);
    }

    @Test
    void checkPersonForViewHistoryTest1() throws UserException, MedicalHistoryException {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        when(personRepository.findByUser(user1)).thenReturn(person1);
        var maybeMedicalHistory = medicalHistoryService.checkPersonForViewHistory(1,user1.getUsername());
        assertEquals(maybeMedicalHistory,medicalHistory);
        verify(medicalHistoryRepository,times(1)).findById(1);
        verify(userRepository,times(1)).findUserByUsername(user1.getUsername());
        verify(personRepository,times(1)).findByUser(user1);
    }

    @Test
    void checkPersonForViewHistoryTest2() throws UserException, MedicalHistoryException {
        person2.setUser(user2);
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(personRepository.findByUser(user2)).thenReturn(person2);
        var maybeMedicalHistory = medicalHistoryService.checkPersonForViewHistory(1,user1.getUsername());
        assertEquals(maybeMedicalHistory,medicalHistory);
        verify(medicalHistoryRepository,times(1)).findById(1);
        verify(userRepository,times(1)).findUserByUsername(user1.getUsername());
        verify(personRepository,times(1)).findByUser(user1);
    }
    @Test
    void checkPersonForViewHistoryFailTest1() {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.empty());
        Exception exception =assertThrows(MedicalHistoryException.class,
                ()->medicalHistoryService.checkPersonForViewHistory(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(NO_MEDICAL_HISTORY_EXCEPTION));
        verify(medicalHistoryRepository,times(1)).findById(1);
    }
    @Test
    void checkPersonForViewHistoryFailTest2() {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(null);
        Exception exception =assertThrows(UserException.class,
                ()->medicalHistoryService.checkPersonForViewHistory(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains(NO_USER_WITH_USERNAME_EXCEPTION));
        verify(medicalHistoryRepository,times(1)).findById(1);
        verify(userRepository,times(1)).findUserByUsername(user1.getUsername());
    }

    @Test
    void checkPersonForViewHistoryFailTest3() {
        when(medicalHistoryRepository.findById(1)).thenReturn(Optional.of(medicalHistory));
        when(userRepository.findUserByUsername(user2.getUsername())).thenReturn(user2);
        when(personRepository.findByUser(user2)).thenReturn(null);
        Exception exception =assertThrows(UserException.class,
                ()->medicalHistoryService.checkPersonForViewHistory(1,user1.getUsername()));
        assertTrue(exception.getMessage().contains("User doesn't have enough permissions"));
        verify(medicalHistoryRepository,times(1)).findById(1);
        verify(userRepository,times(1)).findUserByUsername(user1.getUsername());
        verify(personRepository,times(1)).findByUser(user2);
    }


    @Test
    void getHistoriesOfPatientTest() throws PersonException {
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(person1);
        medicalHistoryService.findHistoriesOfPatient(user1.getUsername());
       verify(personRepository,times(1)).findPersonByUsernameOfUser(user1.getUsername());
       verify(medicalHistoryRepository,times(1)).findByPatient(person1);

    }
    @Test
    void getHistoriesOfPatientFailTest() {
        when(personRepository.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(null);
        Exception exception =assertThrows(PersonException.class,
                ()->medicalHistoryService.findHistoriesOfPatient(user1.getUsername()));
        assertTrue(exception.getMessage().contains(USER_WITHOUT_A_PERSON_EXCEPTION));
        verify(personRepository,times(1)).findPersonByUsernameOfUser(user1.getUsername());
        verify(medicalHistoryRepository,times(0)).findByPatient(person1);

    }
}