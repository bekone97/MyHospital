package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.NameOfProcessRepository;
import com.itacademy.myhospital.service.NameOfProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static com.itacademy.myhospital.constants.Constants.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class NameOfProcessServiceImplTest {
    @Autowired
    private NameOfProcessService nameOfProcessService;

    @MockBean
    private NameOfProcessRepository nameOfProcessRepository;

    private MedicalHistoryProcess medicalHistoryProcess1;
    private MedicalHistoryProcess medicalHistoryProcess2;
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
    private List<NameOfProcess> nameOfProcesses;
    private NameOfProcess nameOfProcess1;
    private NameOfProcess nameOfProcess2;
    private MedicalHistoryDtoWithProcesses medicalHistory;

    @BeforeEach
    public void setUp(){
        nameOfProcess1= NameOfProcess.builder()
                .id(1)
                .name("Urinoteropia")
                .process(Process.builder()
                        .id(2)
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
        nameOfProcess2= NameOfProcess.builder()
                .id(3)
                .name("Paracetamol")
                .process(Process.builder()
                        .id(3)
                        .name("Medication")
                        .build())
                .build();
        nameOfProcesses=new ArrayList<>();
        nameOfProcesses.add(nameOfProcess1);
        medicalHistoryProcess1= MedicalHistoryProcess.builder()
                .id(1)
                .nameOfProcess(nameOfProcess1)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcess2= MedicalHistoryProcess.builder()
                .id(2)
                .nameOfProcess(nameOfProcess2)
                .numberOfDays(1)
                .quantityPerDay(1)
                .status(false)
                .build();
        medicalHistoryProcesses=new ArrayList<>();
        medicalHistoryProcesses.add(medicalHistoryProcess1);
        medicalHistoryProcesses.add(medicalHistoryProcess2);
        medicalHistory = MedicalHistoryDtoWithProcesses.builder()
                .id(1)
                .complain("Dadadada")
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
    }

    @Test
    void findByIdTest() throws NameOfProcessesException {
        when(nameOfProcessRepository.findById(1)).thenReturn(Optional.of(nameOfProcess1));
        nameOfProcessService.findById(1);
        verify(nameOfProcessRepository,times(1)).findById(1);
    }
    @Test
    void findByIdFailTest(){
        when(nameOfProcessRepository.findById(2)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NameOfProcessesException.class,
                ()->nameOfProcessService.findById(2));
        assertTrue(exception.getMessage().contains(NO_NAME_OF_PROCESS_EXCEPTION));
        verify(nameOfProcessRepository,times(1)).findById(2);
    }
    @Test
    void deleteById() throws NameOfProcessesException {
        when(nameOfProcessRepository.existsById(1)).thenReturn(true);
        nameOfProcessService.deleteById(1);
        verify(nameOfProcessRepository,times(1)).existsById(1);
        verify(nameOfProcessRepository,times(1)).deleteById(1);
    }

    @Test
    void checkNameOfProcesses() {
        when(nameOfProcessRepository.findByName(nameOfProcess1.getName())).thenReturn(nameOfProcesses);
        when(nameOfProcessRepository.findByName(nameOfProcess2.getName())).thenReturn(new ArrayList<>());
        var dto = nameOfProcessService.checkNameOfProcesses(medicalHistory);
        assertEquals(nameOfProcess1, dto.getMedicalHistoryProcesses().get(0).getNameOfProcess());
        assertEquals(nameOfProcess2.getName(), dto.getMedicalHistoryProcesses().get(1).getNameOfProcess().getName());
        verify(nameOfProcessRepository, times(1)).findByName(nameOfProcess1.getName());
        verify(nameOfProcessRepository, times(1)).findByName(nameOfProcess2.getName());
    }
}