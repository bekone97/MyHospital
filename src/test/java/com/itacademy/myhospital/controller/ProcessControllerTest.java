package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.NameOfProcess;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.service.MedicalHistoryService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class ProcessControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    @MockBean
    private  PersonService personService;
    @MockBean
    private MedicalHistoryService medicalHistoryService;
    @MockBean
    private Principal principal;

    private MedicalHistoryDtoWithNumberOfProcesses dtoWithNumberOfProcesses;
    private Person person1;
    private Person person2;
    private MedicalHistoryDtoWithProcesses dtoWithProcesses;
    private MedicalHistoryProcess medicalHistoryProcess;
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
    private Diagnosis diagnosis;
    private NameOfProcess nameOfProcess;
    @BeforeEach
    public void setUp() {
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
         nameOfProcess = NameOfProcess.builder()
                .id(1)
                .name("Urinoteropia")
                .process(com.itacademy.myhospital.model.entity.Process.builder()
                        .id(1)
                        .name("PROCEDURE")
                        .build())
                .build();
        medicalHistoryProcess = MedicalHistoryProcess.builder()
                .id(1)
                .medicalHistory(null)
                .nameOfProcess(nameOfProcess)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcesses = new ArrayList<>();
        diagnosis =Diagnosis.builder()
                .personal(person1)
                .name("asdqweq")
                .build();
        medicalHistoryProcesses.add(medicalHistoryProcess);
        dtoWithNumberOfProcesses= MedicalHistoryDtoWithNumberOfProcesses.builder()
                .numberOfProcedures(1)
                .diagnosis(diagnosis)
                .patient(person2)
                .numberOfMedications(0)
                .numberOfOperations(0)
                .build();
        dtoWithProcesses = MedicalHistoryDtoWithProcesses.builder()
                .complain("asldlasmd")
                .patient(person2)
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();

    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void selectQuantityOfProcessesTest() throws Exception {
        when(principal.getName()).thenReturn("user");
        when(personService.findPersonByUsernameOfUser("user")).thenReturn(person1);
        when(medicalHistoryService
                .getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(dtoWithNumberOfProcesses,person1))
                .thenReturn(dtoWithProcesses);
        this.mockMvc.perform(get("/selectProcesses")
                .flashAttr("history",dtoWithNumberOfProcesses))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("history",dtoWithProcesses))
                .andExpect(view().name("process/select-processes"));

        verify(personService,times(1)).findPersonByUsernameOfUser("user");
        verify(medicalHistoryService,times(1))
                .getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(dtoWithNumberOfProcesses,person1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void selectQuantityOfProcessesWrongValidTest() throws Exception {
        diagnosis.setName(" ");
        dtoWithNumberOfProcesses.setNumberOfProcedures(-1);
        dtoWithNumberOfProcesses.setNumberOfMedications(-1);
        dtoWithNumberOfProcesses.setNumberOfOperations(-1);

        this.mockMvc.perform(get("/selectProcesses")
                        .flashAttr("history",dtoWithNumberOfProcesses))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(4))
                .andExpect(view().name("history/history-add-info"));
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE",})
    void selectQuantityOfProcessesFailTest() throws Exception {

        this.mockMvc.perform(get("/selectProcesses")
                        .flashAttr("history",dtoWithNumberOfProcesses))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }
}