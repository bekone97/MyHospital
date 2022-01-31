package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.model.repository.HistoryOfCompletingProcessRepository;
import com.itacademy.myhospital.service.HistoryOfCompletingProcessService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class HistoryOfCompletingProcessServiceImplTest {
    @Autowired
    private HistoryOfCompletingProcessService historyOfCompletingProcessService;
    @MockBean
    private HistoryOfCompletingProcessRepository historyOfCompletingProcessRepository;
    private Person person1;

    private MedicalHistoryProcess medicalHistoryProcess;

    private HistoryOfCompletingProcess historyOfCompletingProcess1;

    private HistoryOfCompletingProcess historyOfCompletingProcess2;

    private List<HistoryOfCompletingProcess> historyOfCompletingProcessList;
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
                    .dateOfBirthday(LocalDate.now().minusDays(10))
                    .user(null)
                    .build();

            medicalHistoryProcess = MedicalHistoryProcess.builder()
                    .id(1)
                    .medicalHistory(MedicalHistory.builder()
                            .id(1)
                            .build())
                    .nameOfProcess(NameOfProcess.builder()
                            .name("asdadq")
                            .process(Process.builder()
                                    .id(1)
                                    .name("OPERATION").build())
                            .build())
                    .numberOfDays(3)
                    .quantityPerDay(1)
                    .status(false)
                    .build();

            historyOfCompletingProcess1= HistoryOfCompletingProcess.builder()
                    .id(1)
                    .resultOfCompleting("ok")
                    .personal(person1)
                    .medicalHistoryProcess(medicalHistoryProcess)
                    .dateOfCompleting(Timestamp.valueOf(LocalDateTime.now()))
                    .build();

            historyOfCompletingProcess2=HistoryOfCompletingProcess.builder()
                    .id(2)
                    .resultOfCompleting("ok")
                    .personal(person1)
                    .medicalHistoryProcess(medicalHistoryProcess)
                    .dateOfCompleting(Timestamp.valueOf(LocalDateTime.now().minusDays(1)))
                    .build();
            historyOfCompletingProcessList=new ArrayList<>();
            historyOfCompletingProcessList.add(historyOfCompletingProcess1);
            historyOfCompletingProcessList.add(historyOfCompletingProcess2);
        }
    @Test
    void findByIdTest() throws HistoryOfCompletingProcessException {
        when(historyOfCompletingProcessRepository.findById(1)).thenReturn(Optional.of(historyOfCompletingProcess1));
        historyOfCompletingProcessService.findById(1);
        verify(historyOfCompletingProcessRepository,times(1)).findById(1);
    }
    @Test
    void findByIdFailTest(){
        when(historyOfCompletingProcessRepository.findById(1)).thenReturn(Optional.empty());
        Exception exception = assertThrows(HistoryOfCompletingProcessException.class,
                ()->historyOfCompletingProcessService.findById(1));
        assertTrue(exception.getMessage().contains(NO_COMPLETING_PROCESS_WITH_ID));
        verify(historyOfCompletingProcessRepository,times(1)).findById(1);
    }

    @Test
    void deleteByIdTest() throws HistoryOfCompletingProcessException {
        when(historyOfCompletingProcessRepository.existsById(1)).thenReturn(true);
        historyOfCompletingProcessService.deleteById(1);
        verify(historyOfCompletingProcessRepository,times(1)).existsById(1);
        verify(historyOfCompletingProcessRepository,times(1)).deleteById(1);
    }
    @Test
    void deleteByIdFailTest(){
        when(historyOfCompletingProcessRepository.existsById(1)).thenReturn(false);
        Exception exception = assertThrows(HistoryOfCompletingProcessException.class,
                ()->historyOfCompletingProcessService.deleteById(1));
        assertTrue(exception.getMessage().contains(NO_COMPLETING_PROCESS_WITH_ID));
        verify(historyOfCompletingProcessRepository,times(1)).existsById(1);
        verify(historyOfCompletingProcessRepository,times(0)).deleteById(1);
    }

    @Test
    void checkNumberOfExecutionsAndCreateNewExecutionTest() throws HistoryOfCompletingProcessException {
        String result = "done";
        when(historyOfCompletingProcessRepository.findByMedicalHistoryProcess(medicalHistoryProcess))
                .thenReturn(historyOfCompletingProcessList);
         historyOfCompletingProcessService
                .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,result);
        assertTrue(medicalHistoryProcess.isStatus());
        verify(historyOfCompletingProcessRepository,times(1))
                .findByMedicalHistoryProcess(medicalHistoryProcess);
    }
    @Test
    void checkNumberOfExecutionsAndCreateNewExecutionFailTest(){
        medicalHistoryProcess.setNumberOfDays(2);
        when(historyOfCompletingProcessRepository.findByMedicalHistoryProcess(medicalHistoryProcess))
                .thenReturn(historyOfCompletingProcessList);
        Exception exception = assertThrows(HistoryOfCompletingProcessException.class,
                ()->historyOfCompletingProcessService
                        .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,"asda"));
        assertTrue(exception.getMessage().contains(MEDICAL_HISTORY_PROCESS_COMPLETED_EXCEPTION));
        verify(historyOfCompletingProcessRepository,times(1))
                .findByMedicalHistoryProcess(medicalHistoryProcess);

    }

}