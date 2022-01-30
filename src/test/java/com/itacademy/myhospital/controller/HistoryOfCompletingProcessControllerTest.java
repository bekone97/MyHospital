package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.service.HistoryOfCompletingProcessService;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import com.itacademy.myhospital.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class HistoryOfCompletingProcessControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private HistoryOfCompletingProcessService historyOfCompletingProcessService;
    @MockBean
    private PersonService personService;
    @MockBean
    private MedicalHistoryProcessService medicalHistoryProcessService;

    @MockBean
    private Principal principal;

    private Person person1;

    private MedicalHistoryProcess medicalHistoryProcess;

    private HistoryOfCompletingProcess historyOfCompletingProcess1;

    private HistoryOfCompletingProcess historyOfCompletingProcess2;

    private List<HistoryOfCompletingProcess> historyOfCompletingProcessList;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
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
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();

        historyOfCompletingProcess1=HistoryOfCompletingProcess.builder()
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
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void executionOfProcessTest() throws Exception {
        var result = "ok";
        when(principal.getName()).thenReturn("user");
        when(personService.findPersonByUsernameOfUser("user")).thenReturn(person1);
        when(medicalHistoryProcessService.findById(1)).thenReturn(medicalHistoryProcess);
        when(historyOfCompletingProcessService
                .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,result)).thenReturn(true);
        this.mockMvc.perform(post("/executionProcess/1")
                        .param("result",result))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + medicalHistoryProcess.getMedicalHistory().getId()));
        verify(personService,times(1)).findPersonByUsernameOfUser("user");
        verify(medicalHistoryProcessService,times(1)).findById(1);
        verify(historyOfCompletingProcessService,times(1))
                .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,result);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void executionOfProcessFailTest() throws Exception {
        var result = "ok";
        when(principal.getName()).thenReturn("user");
        when(personService.findPersonByUsernameOfUser("user")).thenReturn(person1);
        when(medicalHistoryProcessService.findById(1)).thenReturn(medicalHistoryProcess);
        when(historyOfCompletingProcessService
                .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,result))
                .thenThrow(HistoryOfCompletingProcessException.class);
        this.mockMvc.perform(post("/executionProcess/1")
                        .param("result",result))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(personService,times(1)).findPersonByUsernameOfUser("user");
        verify(medicalHistoryProcessService,times(1)).findById(1);
        verify(historyOfCompletingProcessService,times(1))
                .checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess,person1,result);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void executionOfProcessFailTest2() throws Exception {
        var result = "ok";
        when(principal.getName()).thenReturn("user");
        when(personService.findPersonByUsernameOfUser("user")).thenReturn(person1);
        when(medicalHistoryProcessService.findById(1)).thenThrow(MedicalHistoryProcessException.class);

        this.mockMvc.perform(post("/executionProcess/1")
                        .param("result",result))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(personService,times(1)).findPersonByUsernameOfUser("user");
        verify(medicalHistoryProcessService,times(1)).findById(1);

    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void executionOfProcessFailTest3() throws Exception {
        var result = "ok";
        this.mockMvc.perform(post("/executionProcess/1")
                        .param("result",result))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void processExecutionHistoryTest() throws Exception {
        when(medicalHistoryProcessService.findById(1)).thenReturn(medicalHistoryProcess);
        when(historyOfCompletingProcessService.findByMedicalHistoryProcess(medicalHistoryProcess))
                .thenReturn(historyOfCompletingProcessList);
        this.mockMvc.perform(get("/processExecutionHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("executionHistory",historyOfCompletingProcessList))
                .andExpect(model().attribute("process",medicalHistoryProcess))
                .andExpect(view().name("historyOfCompletingProcess/executions-of-process"))
                        .andExpect(xpath("//*[@id='executionsProcessTbody']/div").nodeCount(2));

        verify(medicalHistoryProcessService,times(1)).findById(1);
        verify(historyOfCompletingProcessService,times(1)).findByMedicalHistoryProcess(medicalHistoryProcess);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void processExecutionHistoryFailTest() throws Exception {
        when(medicalHistoryProcessService.findById(1)).thenThrow(MedicalHistoryProcessException.class);
        this.mockMvc.perform(get("/processExecutionHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));

        verify(medicalHistoryProcessService,times(1)).findById(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void processExecutionHistoryFailTest2() throws Exception {
        when(medicalHistoryProcessService.findById(1)).thenThrow(MedicalHistoryProcessException.class);
        this.mockMvc.perform(get("/processExecutionHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void deleteProcessTest() throws Exception {
        when(medicalHistoryProcessService.findById(1)).thenReturn(medicalHistoryProcess);
        when(historyOfCompletingProcessService
                .removeHistoryOfCompletingProcessesByMedicalHistoryProcess(medicalHistoryProcess)).thenReturn(true);
        when(medicalHistoryProcessService.deleteById(1)).thenReturn(true);
        this.mockMvc.perform(get("/deleteProcess/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + medicalHistoryProcess.getMedicalHistory().getId()));

        verify(medicalHistoryProcessService,times(1)).findById(1);
        verify(historyOfCompletingProcessService,times(1))
                .removeHistoryOfCompletingProcessesByMedicalHistoryProcess(medicalHistoryProcess);
        verify(medicalHistoryProcessService,times(1)).deleteById(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void deleteProcessFailTest() throws Exception {
        when(medicalHistoryProcessService.findById(1)).thenThrow(MedicalHistoryProcessException.class);
        this.mockMvc.perform(get("/deleteProcess/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(view().name("error/405"));

        verify(medicalHistoryProcessService,times(1)).findById(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void deleteProcessFailTest2() throws Exception {
        this.mockMvc.perform(get("/deleteProcess/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }
}