package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.MedicalHistoryProcessRepository;
import com.itacademy.myhospital.model.repository.NameOfProcessRepository;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class MedicalHistoryProcessServiceImplTest {

    @Autowired
    private MedicalHistoryProcessService medicalHistoryProcessService;

    @MockBean
    private MedicalHistoryProcessRepository medicalHistoryProcessRepository;

    @MockBean
    private NameOfProcessRepository nameOfProcessRepository;

    private MedicalHistoryProcess medicalHistoryProcess1;
    private MedicalHistoryProcess medicalHistoryProcess2;
    private MedicalHistoryProcess medicalHistoryProcess3;
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
    private NameOfProcess nameOfProcess1;
    private NameOfProcess nameOfProcess2;
    private NameOfProcess nameOfProcess3;
    private Process operation;
    private Process procedure;
    private Process medication;
    private Person patient;
    private Map<Process, Integer> mapOfProcesses;
    private MedicalHistoryDtoWithNumberOfProcesses medicalHistoryDtoWithNumberOfProcesses;
    private MedicalHistoryDtoWithProcesses medicalHistoryDtoWithProcesses;
    private List<NameOfProcess> nameOfProcesses;

    @BeforeEach
    public void setUp() {
        operation = Process.builder()
                .id(1)
                .name("OPERATION")
                .build();
        procedure = Process.builder()
                .id(2)
                .name("PROCEDURE")
                .build();
        medication = Process.builder()
                .id(3)
                .name("MEDICATION")
                .build();
        mapOfProcesses = new HashMap<>();
        mapOfProcesses.put(operation, 1);
        mapOfProcesses.put(procedure, 1);
        mapOfProcesses.put(medication, 1);
        nameOfProcess2 = NameOfProcess.builder()
                .id(2)
                .name("Urinoteropia")
                .process(procedure)
                .build();
        nameOfProcess1 = NameOfProcess.builder()
                .id(1)
                .name("Zhirootsos")
                .process(operation)
                .build();
        nameOfProcess3 = NameOfProcess.builder()
                .id(3)
                .name("Paracetamol")
                .process(medication)
                .build();
        medicalHistoryProcess1 = MedicalHistoryProcess.builder()
                .id(1)
                .nameOfProcess(nameOfProcess2)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcess2 = MedicalHistoryProcess.builder()
                .id(2)
                .nameOfProcess(nameOfProcess1)
                .numberOfDays(1)
                .quantityPerDay(1)
                .status(false)
                .build();
        medicalHistoryProcess3 = MedicalHistoryProcess.builder()
                .id(3)
                .nameOfProcess(nameOfProcess3)
                .numberOfDays(1)
                .quantityPerDay(1)
                .status(false)
                .build();
        patient = Person.builder()
                .firstName("dadada")
                .build();
        medicalHistoryProcesses = new ArrayList<>();
        medicalHistoryProcesses.add(medicalHistoryProcess1);
        medicalHistoryProcesses.add(medicalHistoryProcess2);
        medicalHistoryProcesses.add(medicalHistoryProcess3);
        medicalHistoryDtoWithProcesses = MedicalHistoryDtoWithProcesses.builder()
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .patient(patient)
                .complain("dadadada")
                .build();
        medicalHistoryDtoWithNumberOfProcesses = MedicalHistoryDtoWithNumberOfProcesses.builder()
                .numberOfOperations(1)
                .numberOfProcedures(1)
                .numberOfMedications(1)
                .complain("dadadada")
                .patient(patient)
                .build();
        nameOfProcesses=new ArrayList<>();
        nameOfProcesses.add(nameOfProcess3);
    }

    @Test
    void findByIdTest() throws MedicalHistoryProcessException {
        when(medicalHistoryProcessRepository.findById(1)).thenReturn(Optional.of(medicalHistoryProcess1));
        medicalHistoryProcessService.findById(1);
        verify(medicalHistoryProcessRepository, times(1)).findById(1);
    }

    @Test
    void findByIdFailTest() {
        when(medicalHistoryProcessRepository.findById(4)).thenReturn(Optional.empty());
        Exception exception = assertThrows(MedicalHistoryProcessException.class,
                () -> medicalHistoryProcessService.findById(4));
        assertTrue(exception.getMessage().contains("There is no medical history process with id :"));
        verify(medicalHistoryProcessRepository, times(1)).findById(4);
    }

    @Test
    void deleteByIdTest() throws MedicalHistoryProcessException {
        when(medicalHistoryProcessRepository.existsById(1)).thenReturn(true);
        medicalHistoryProcessService.deleteById(1);
        verify(medicalHistoryProcessRepository, times(1)).existsById(1);
        verify(medicalHistoryProcessRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteByIdFailTest() {
        when(medicalHistoryProcessRepository.existsById(4)).thenReturn(false);
        Exception exception = assertThrows(MedicalHistoryProcessException.class,
                () -> medicalHistoryProcessService.deleteById(4));
        assertTrue(exception.getMessage().contains("There is no medical history process with id :"));
        verify(medicalHistoryProcessRepository, times(1)).existsById(4);
        verify(medicalHistoryProcessRepository, times(0)).deleteById(4);
    }

    @Test
    void createMedicalHistoryProcessesAndAddToHistory() {
        var maybeMedicalHistoryDtoWithProcesses = medicalHistoryProcessService
                .createMedicalHistoryProcessesAndAddToHistory(mapOfProcesses,
                        medicalHistoryDtoWithNumberOfProcesses);
        assertEquals(medicalHistoryDtoWithProcesses.getComplain(),maybeMedicalHistoryDtoWithProcesses.getComplain());
        for (int i =0;i<medicalHistoryDtoWithProcesses.getMedicalHistoryProcesses().size();i++){
            assertEquals(medicalHistoryDtoWithProcesses.getMedicalHistoryProcesses().get(i).getNameOfProcess().getProcess(),
                    maybeMedicalHistoryDtoWithProcesses.getMedicalHistoryProcesses().get(i).getNameOfProcess().getProcess());
        }
        assertEquals(medicalHistoryDtoWithProcesses.getPatient(),maybeMedicalHistoryDtoWithProcesses.getPatient());

    }

    @Test
    void checkAndSaveMedicalHistoryProcessTest1() {
        when(nameOfProcessRepository.findByName(nameOfProcess3.getName())).thenReturn(nameOfProcesses);
        var testMedicalHistoryProcess= medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess3);
        assertEquals(nameOfProcess3,testMedicalHistoryProcess.getNameOfProcess());
        verify(nameOfProcessRepository,(times(1))).findByName(nameOfProcess3.getName());
    }
    @Test
    void checkAndSaveMedicalHistoryProcessTest2() {
        when(nameOfProcessRepository.findByName(nameOfProcess3.getName())).thenReturn(new ArrayList<>());
        var testMedicalHistoryProcess= medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess3);
        assertEquals(nameOfProcess3,testMedicalHistoryProcess.getNameOfProcess());
        assertEquals(nameOfProcess3.getName(),testMedicalHistoryProcess.getNameOfProcess().getName());
        assertEquals(nameOfProcess3.getProcess(),testMedicalHistoryProcess.getNameOfProcess().getProcess());
        verify(nameOfProcessRepository,(times(1))).findByName(nameOfProcess3.getName());
    }
}