package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Process;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import static com.itacademy.myhospital.constants.Constants.*;
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
class MedicalHistoryControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalHistoryService medicalHistoryService;

    @MockBean
    private ProcessService processService;

    @MockBean
    private MedicalHistoryProcessService medicalHistoryProcessService;

    @MockBean
    private DiagnosisService diagnosisService;

    @MockBean
    private NameOfProcessService nameOfProcessService;

    @MockBean
    private Principal principal;

    private User user1;
    private User user2;
    List<Person> personList;
    private Person person1;
    private Person person2;
    private Page<Person> personsPage;
    private List<MedicalHistory> medicalHistories;
    private MedicalHistory medicalHistory;
    private MedicalHistory medicalHistory2;
    private NameOfProcess nameOfProcess1;
    private Diagnosis diagnosis1;
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
    private MedicalHistoryProcess medicalHistoryProcess;
    private MedicalHistoryDtoWithProcesses dtoWithProcesses;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        user1 = User.builder()
                .id(1)
                .username("user")
                .password("password")
                .email("User@mail.ru")
                .img("asldaldk")
                .build();
        user2 = User.builder()
                .id(1)
                .username("asdadw")
                .password("asdadaw")
                .email("Adsdn@mail.ru")

                .build();

        person1 = Person.builder()
                .id(1)
                .firstName("Myachin")
                .surname("Artem")
                .patronymic("Valerevich")
                .phoneNumber("297342938")
                .address("g.Grodno")
                .keyForUser("asl,qlw,q")
                .dateOfBirthday(LocalDate.now().minusDays(10))
                .user(user1)
                .build();
        personList = new ArrayList<>();
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
        Pageable pageable = PageRequest.of(1, 2);
        personsPage = new PageImpl<>(personList, pageable, 2);
        nameOfProcess1 = NameOfProcess.builder()
                .id(1)
                .name("Urinoteropia")
                .process(com.itacademy.myhospital.model.entity.Process.builder()
                        .id(1)
                        .name("PROCEDURE")
                        .build())
                .build();
        medicalHistoryProcess = MedicalHistoryProcess.builder()
                .id(1)
                .medicalHistory(medicalHistory)
                .nameOfProcess(nameOfProcess1)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcesses = new ArrayList<>();
        medicalHistoryProcesses.add(medicalHistoryProcess);
        diagnosis1 = Diagnosis.builder()
                .id(1)
                .name("ORVI")
                .personal(person1)
                .build();
        medicalHistory = MedicalHistory.builder()
                .id(1)
                .dischargeStatus(false)
                .diagnosis(diagnosis1)
                .complain("Dadadada")
                .patient(person2)
                .receiptDate(Timestamp.valueOf(LocalDateTime.now()))
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
        medicalHistory2 = MedicalHistory.builder()
                .id(2)
                .dischargeStatus(false)
                .diagnosis(diagnosis1)
                .complain("Dadadada")
                .patient(person2)
                .receiptDate(Timestamp.valueOf(LocalDateTime.now()))
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
        medicalHistories = new ArrayList<>();
        medicalHistories.add(medicalHistory);
        medicalHistories.add(medicalHistory2);
        dtoWithProcesses = MedicalHistoryDtoWithProcesses.builder()
                .complain("asldlasmd")
                .patient(person2)
                .diagnosis(diagnosis1)
                .medicalHistoryProcesses(medicalHistoryProcesses)
                .build();
    }


    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void historyByIdTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.checkPersonForViewHistory(1, principal.getName())).thenReturn(medicalHistory);
        this.mockMvc.perform(get("/history/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("history", medicalHistory))
                .andExpect(view().name("history/history-info"));

        verify(medicalHistoryService, times(1)).checkPersonForViewHistory(1, user1.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void historyByIdFailTest1() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.checkPersonForViewHistory(15, principal.getName())).thenThrow(UserException.class);
        this.mockMvc.perform(get("/history/15"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(medicalHistoryService, times(1)).checkPersonForViewHistory(15, user1.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void historyByIdFailTest2() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.checkPersonForViewHistory(15, principal.getName())).thenThrow(MedicalHistoryException.class);
        this.mockMvc.perform(get("/history/15"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(medicalHistoryService, times(1)).checkPersonForViewHistory(15, user1.getUsername());
    }

    @Test
    void historyByIdFailTest3() throws Exception {
        this.mockMvc.perform(get("/history/15"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_PAGE));

        verify(medicalHistoryService, times(0)).checkPersonForViewHistory(15, user1.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void checkNewHistoryTest() throws Exception {
        when(nameOfProcessService.checkNameOfProcesses(dtoWithProcesses)).thenReturn(dtoWithProcesses);
        when(medicalHistoryService.createAndSaveNewMedicalHistoryFromDto(dtoWithProcesses)).thenReturn(medicalHistory);
        this.mockMvc.perform(post("/checkHistory")
                        .flashAttr("history", dtoWithProcesses))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/" + dtoWithProcesses.getPatient().getId()));
        verify(nameOfProcessService, times(1)).checkNameOfProcesses(dtoWithProcesses);
        verify(medicalHistoryService, times(1)).createAndSaveNewMedicalHistoryFromDto(dtoWithProcesses);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void checkNewHistoryWrongValidTest() throws Exception {
        nameOfProcess1.setName("");
        medicalHistoryProcess.setNumberOfDays(0);
        medicalHistoryProcess.setQuantityPerDay(0);
        this.mockMvc.perform(post("/checkHistory")
                        .flashAttr("history", dtoWithProcesses))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(view().name("process/select-processes"));
        verify(nameOfProcessService, times(0)).checkNameOfProcesses(dtoWithProcesses);
        verify(medicalHistoryService, times(0)).createAndSaveNewMedicalHistoryFromDto(dtoWithProcesses);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void checkNewHistoryWithoutDoctorRoleTest() throws Exception {
        nameOfProcess1.setName("");
        medicalHistoryProcess.setNumberOfDays(0);
        medicalHistoryProcess.setQuantityPerDay(0);
        this.mockMvc.perform(post("/checkHistory")
                        .flashAttr("history", dtoWithProcesses))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addNewHistoryTest() throws Exception {
        List<Diagnosis> diagnoses = new ArrayList<>();
        diagnoses.add(diagnosis1);
        diagnoses.add(new Diagnosis());
        var historyDto =
                MedicalHistoryDtoWithNumberOfProcesses.builder()
                        .patient(person2)
                        .diagnosis(new Diagnosis())
                        .numberOfOperations(0)
                        .numberOfProcedures(0)
                        .numberOfMedications(0)
                        .build();
        when(principal.getName()).thenReturn(user1.getUsername());
        when(diagnosisService.getDiagnosesOfPerson(user1.getUsername())).thenReturn(diagnoses);
        when(personService.findById(1)).thenReturn(person2);
        this.mockMvc.perform(get("/addNewHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("history", historyDto))
                .andExpect(model().attribute("diagnoses", diagnoses))
                .andExpect(view().name("history/history-add-info"))
                .andExpect(xpath("//*[@id='diagnoses']/option").nodeCount(2));
        verify(diagnosisService, times(1)).getDiagnosesOfPerson(user1.getUsername());
        verify(personService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addNewHistoryFailTest() throws Exception {
        List<Diagnosis> diagnoses = new ArrayList<>();
        diagnoses.add(diagnosis1);
        when(principal.getName()).thenReturn(user1.getUsername());
        when(diagnosisService.getDiagnosesOfPerson(user1.getUsername())).thenReturn(diagnoses);
        when(personService.findById(10)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/addNewHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(diagnosisService, times(1)).getDiagnosesOfPerson(user1.getUsername());
        verify(personService, times(1)).findById(10);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void addNewHistoryFailTestWithoutDoctorRole() throws Exception {
        this.mockMvc.perform(get("/addNewHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(diagnosisService, times(0)).getDiagnosesOfPerson(user1.getUsername());
        verify(personService, times(0)).findById(10);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addProcessToMedicalHistoryTest() throws Exception {
        List<Process> processes = new ArrayList<>();
        processes.add(Process.builder()
                .id(OPERATION_ID)
                .name("OPERATION")
                .build());
        processes.add(Process.builder()
                .id(PROCEDURE_ID)
                .name("PROCEDURE")
                .build());
        processes.add(Process.builder()
                .id(MEDICATION_ID)
                .name("MEDICATION")
                .build());
        var medicalHistoryProcess = MedicalHistoryProcess.builder()
                .medicalHistory(medicalHistory)
                .nameOfProcess(new NameOfProcess())
                .build();
        when(processService.findAll()).thenReturn(processes);
        when(medicalHistoryService.findById(1)).thenReturn(medicalHistory);
        this.mockMvc.perform(get("/addProcessToMedicalHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("processes", processes))
                .andExpect(model().attribute("medicalHistoryProcess", medicalHistoryProcess))
                .andExpect(view().name("history/add-process-to-medical-history"))
                .andExpect(xpath("//*[@id='selectOfProcess']/div").nodeCount(3));

        verify(processService, times(1)).findAll();
        verify(medicalHistoryService, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addProcessToMedicalHistoryWithTrueStatusTest() throws Exception {
        medicalHistory.setDischargeStatus(true);
        when(medicalHistoryService.findById(10)).thenReturn(medicalHistory);
        this.mockMvc.perform(get("/addProcessToMedicalHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + medicalHistory.getId()));
        verify(medicalHistoryService, times(1)).findById(10);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addProcessToMedicalHistoryWithHistoryException() throws Exception {
        when(medicalHistoryService.findById(10)).thenThrow(MedicalHistoryException.class);
        this.mockMvc.perform(get("/addProcessToMedicalHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(medicalHistoryService, times(1)).findById(10);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void addProcessToMedicalHistoryWithoutRoleDoctor() throws Exception {
        when(medicalHistoryService.findById(10)).thenThrow(MedicalHistoryException.class);
        this.mockMvc.perform(get("/addProcessToMedicalHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addProcessToMedicalHistoryWithDischargedStatusTest() throws Exception {
        medicalHistory.setDischargeStatus(true);
        when(medicalHistoryService.findById(1)).thenReturn(medicalHistory);
        this.mockMvc.perform(get("/addProcessToMedicalHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + medicalHistory.getId()));


        verify(medicalHistoryService, times(1)).findById(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void saveProcessToMedicalHistoryTest() throws Exception {
        when(medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess)).thenReturn(medicalHistoryProcess);
        medicalHistoryProcess.setMedicalHistory(medicalHistory);
        this.mockMvc.perform(post("/addProcessToMedicalHistory")
                        .flashAttr("medicalHistoryProcess", medicalHistoryProcess))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + medicalHistoryProcess.getMedicalHistory().getId()));

        verify(medicalHistoryProcessService, times(1)).checkAndSaveMedicalHistoryProcess(medicalHistoryProcess);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void saveProcessToMedicalHistoryWithWrongValidTest() throws Exception {
        medicalHistoryProcess.setNumberOfDays(0);
        medicalHistoryProcess.setQuantityPerDay(0);
        nameOfProcess1.setName("");
        when(medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess)).thenReturn(medicalHistoryProcess);
        medicalHistoryProcess.setMedicalHistory(medicalHistory);
        this.mockMvc.perform(post("/addProcessToMedicalHistory")
                        .flashAttr("medicalHistoryProcess", medicalHistoryProcess))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(view().name("history/add-process-to-medical-history"));
        verify(medicalHistoryProcessService, times(0)).checkAndSaveMedicalHistoryProcess(medicalHistoryProcess);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void saveProcessToMedicalHistoryWithoutRoleDoctor() throws Exception {
        when(medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess)).thenReturn(medicalHistoryProcess);
        medicalHistoryProcess.setMedicalHistory(medicalHistory);
        this.mockMvc.perform(post("/addProcessToMedicalHistory")
                        .flashAttr("medicalHistoryProcess", medicalHistoryProcess))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void dischargePatientTest() throws Exception {
        when(medicalHistoryService.dischargePatient(1)).thenReturn(true);
        this.mockMvc.perform(get("/dischargePatient/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/history/" + 1));

        verify(medicalHistoryService,times(1)).dischargePatient(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void dischargePatientFailTest() throws Exception {
        when(medicalHistoryService.dischargePatient(10)).thenThrow(MedicalHistoryException.class);
        this.mockMvc.perform(get("/dischargePatient/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(medicalHistoryService,times(1)).dischargePatient(10);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void dischargePatientFailTestWithoutRole() throws Exception {
        when(medicalHistoryService.dischargePatient(10)).thenThrow(MedicalHistoryException.class);
        this.mockMvc.perform(get("/dischargePatient/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(medicalHistoryService,times(0)).dischargePatient(10);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void getAllHistoriesTest() throws Exception {
        when(personService.findById(1)).thenReturn(person1);
        when(medicalHistoryService.findByPatient(person1)).thenReturn(medicalHistories);
        this.mockMvc.perform(get("/openHistory/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("histories",medicalHistories))
                .andExpect(view().name("history/histories-of-patient"))
                .andExpect(xpath("//*[@id='historiesTbody']/div").nodeCount(2));
        verify(personService,times(1)).findById(1);
        verify(medicalHistoryService,times(1)).findByPatient(person1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void getAllHistoriesFailTest() throws Exception {
        when(personService.findById(10)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/openHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService,times(1)).findById(10);
        verify(medicalHistoryService,times(0)).findByPatient(person1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void getAllHistoriesTestWithoutRoleNurse() throws Exception {
        when(personService.findById(10)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/openHistory/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user")
    void getHistoriesOfPatientTestWithHistories() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.findHistoriesOfPatient(user1.getUsername())).thenReturn(medicalHistories);
        mockMvc.perform(get("/myMedicalHistory"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("histories",medicalHistories))
                .andExpect(model().attribute("isPersonHasHistory",true))
                .andExpect(view().name("history/patient-medical-history"));

        verify(medicalHistoryService,times(1)).findHistoriesOfPatient(user1.getUsername());

    }
    @Test
    @WithMockUser(username = "user")
    void getHistoriesOfPatientTestWithoutHistories() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.findHistoriesOfPatient(user1.getUsername())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/myMedicalHistory"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("isPersonHasHistory",false))
                .andExpect(view().name("history/patient-medical-history"));

        verify(medicalHistoryService,times(1)).findHistoriesOfPatient(user1.getUsername());

    }
    @Test
    @WithMockUser(username = "user")
    void getHistoriesOfPatientFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(medicalHistoryService.findHistoriesOfPatient(user1.getUsername())).thenThrow(PersonException.class);
        mockMvc.perform(get("/myMedicalHistory"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(medicalHistoryService,times(1)).findHistoriesOfPatient(user1.getUsername());

    }
}