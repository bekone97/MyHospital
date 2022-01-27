package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.DiagnosisException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.exception.NameOfProcessesException;
import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.repository.ProcessRepository;
import com.itacademy.myhospital.service.ProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class ProcessServiceImplTest {
    public static final String NO_PROCESS_WITH_ID_EXCEPTION = "There is no process with id : ";
@Autowired
private ProcessService processService;
@MockBean
private ProcessRepository processRepository;
    private Process operation;
    private Process procedure;
    private Process medication;
@BeforeEach
public void setUp(){

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
}
    @Test
    void findByIdFailTest() {
    when(processRepository.findById(4)).thenReturn(Optional.ofNullable(null));
    Exception exception = assertThrows(ProcessException.class,
            ()->processService.findById(4));
    assertTrue(exception.getMessage().contains(NO_PROCESS_WITH_ID_EXCEPTION));
    }

    @Test
    void deleteByIdFailTest() {
        when(processRepository.existsById(4)).thenReturn(false);
        Exception exception = assertThrows(ProcessException.class,
                ()->processService.deleteById(4));
        assertTrue(exception.getMessage().contains(NO_PROCESS_WITH_ID_EXCEPTION));
    }
    @Test
    void deleteByIdTest() throws ProcessException{
        when(processRepository.existsById(1)).thenReturn(true);
        processService.deleteById(1);
        verify(processRepository,times(1)).existsById(1);
        verify(processRepository,times(1)).deleteById(1);
    }

    @Test
    void getMapOfProcesses() throws ProcessException {
    when(processRepository.findById(1)).thenReturn(Optional.of(operation));
    when(processRepository.findById(2)).thenReturn(Optional.of(procedure));
    when(processRepository.findById(3)).thenReturn(Optional.of(medication));
        Map<Process,Integer> maybeMap = processService.getMapOfProcesses(1,2,3);
        assertEquals(3,maybeMap.size());
        assertEquals(3,maybeMap.get(medication));
        assertEquals(2,maybeMap.get(procedure));
        assertEquals(1,maybeMap.get(operation));
        verify(processRepository,times(1)).findById(1);
        verify(processRepository,times(1)).findById(2);
        verify(processRepository,times(1)).findById(3);
    }
}