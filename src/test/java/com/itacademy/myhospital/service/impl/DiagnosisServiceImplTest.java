package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.repository.DiagnosisRepository;
import com.itacademy.myhospital.model.repository.PersonRepository;
import com.itacademy.myhospital.service.DiagnosisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class DiagnosisServiceImplTest {
    @Autowired
    private DiagnosisService diagnosisService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private DiagnosisRepository diagnosisRepository;

    private Person person1;
    private Diagnosis diagnosis;
    private List<Diagnosis> diagnoses;
    @BeforeEach
    public void setUp(){
        person1 = Person.builder()
                .id(1)
                .firstName("Myachin")
                .surname("Artem")
                .patronymic("Valerevich")
                .phoneNumber("297342938")
                .address("g.Grodno")
                .keyForUser("asl,qlw,q")
                .dateOfBirthday(LocalDate.now())
                .user(null)
                .build();
        diagnosis= Diagnosis.builder()
                .personal(person1)
                .name("diagnosis")
                .build();
    diagnoses=new ArrayList<>();
    diagnoses.add(diagnosis);
    }
    @Test
    void findOrCreateDiagnosisTest1() {
        when(diagnosisRepository.findByNameAndPersonal(diagnosis.getName(),diagnosis.getPersonal())).thenReturn(diagnoses);
        var maybeDiagnoses = diagnosisService.findOrCreateDiagnosis(diagnosis.getName(),diagnosis.getPersonal());
        assertEquals(maybeDiagnoses,diagnoses.stream().findAny().get());
        verify(diagnosisRepository,times(1)).findByNameAndPersonal(diagnosis.getName(),diagnosis.getPersonal());
    }
    @Test
    void findOrCreateDiagnosisTest2() {
        when(diagnosisRepository.findByNameAndPersonal(diagnosis.getName(),diagnosis.getPersonal())).thenReturn(new ArrayList<>());
        var maybeDiagnoses = diagnosisService.findOrCreateDiagnosis(diagnosis.getName(),diagnosis.getPersonal());
        assertEquals(maybeDiagnoses.getName(),diagnosis.getName());
        assertEquals(maybeDiagnoses.getPersonal(),diagnosis.getPersonal());
        verify(diagnosisRepository,times(1)).findByNameAndPersonal(diagnosis.getName(),diagnosis.getPersonal());
    }

    @Test
    void getDiagnosesOfPersonTest() {
       var person = Person.builder()
                .id(1)
                .build();
        when(personRepository.findPersonByUsernameOfUser("username")).thenReturn(person);
        when(diagnosisRepository.findByPersonal(person)).thenReturn(diagnoses);
        var maybeDiagnoses=diagnosisService.getDiagnosesOfPerson("username");
        assertTrue(maybeDiagnoses.size()==1);
        verify(personRepository,times(1)).findPersonByUsernameOfUser("username");
        verify(diagnosisRepository,times(1)).findByPersonal(person);
    }
}