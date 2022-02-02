package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.model.entity.*;
import com.itacademy.myhospital.service.PersonService;
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
class PersonControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

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
    private MedicalHistoryProcess medicalHistoryProcess1;
    private PersonDto personDto;

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
        medicalHistoryProcess1 = MedicalHistoryProcess.builder()
                .id(1)
                .medicalHistory(medicalHistory)
                .nameOfProcess(nameOfProcess1)
                .numberOfDays(2)
                .quantityPerDay(3)
                .status(false)
                .build();
        medicalHistoryProcesses = new ArrayList<>();
        medicalHistoryProcesses.add(medicalHistoryProcess1);
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
        personDto = PersonDto.builder()
                .firstName("Myachin")
                .surname("Artem")
                .patronymic("Valerevich")
                .phoneNumber("297342938")
                .address("g.Grodno")
                .keyForUser("asl,qlw,q")
                .dateOfBirthday(LocalDate.now().minusDays(10).toString())
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void personsTest() throws Exception {
        when(personService.findAll(1, "id", "asc")).thenReturn(personsPage);
        this.mockMvc.perform(get("/persons/1")
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(5))
                .andExpect(model().attribute("sortField", "id"))
                .andExpect(model().attribute("sortDirection", "asc"))
                .andExpect(model().attribute("reverseSortDirection", "desc"))
                .andExpect(model().attribute("page", personsPage))
                .andExpect(model().attribute("persons", personsPage.getContent()))
                .andExpect(view().name("person/persons"))
                .andExpect(xpath("//*[@id='personBody']/div").nodeCount(2));

        verify(personService, times(1)).findAll(1, "id", "asc");
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void personsFailTest1() throws Exception {
        when(personService.findAll(10, "id", "asc")).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/persons/10")
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/1?sortField=surname&sortDirection=asc"));


        verify(personService, times(1)).findAll(10, "id", "asc");
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void personsFailTest2() throws Exception {
        when(personService.findAll(10, "id", "asc")).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/persons/10")
                        .param("sortField", "id")
                        .param("sortDirection", "asc"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));


    }

    @Test
    void personsFailTest3() throws Exception {
        when(personService.findAll(10, "id", "asc")).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/persons/10")
                        .param(SORT_FIELD_FOR_MODEL, "id")
                        .param(SORT_DIRECTION_FOR_MODEL, ASC_FOR_SORT_DIRECTION))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void personByIdTest() throws Exception {
        when(personService.findById(1)).thenReturn(person1);
        when(personService.findCurrentHistories(person1)).thenReturn(medicalHistories);
        this.mockMvc.perform(get("/person/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(2))
                .andExpect(model().attribute(HISTORIES_FOR_MODEL, medicalHistories))
                .andExpect(model().attribute(PERSON_FOR_MODEL, person1))
                .andExpect(view().name("person/person-info"))
                .andExpect(xpath("//*[@id='historiesTBody']/div").nodeCount(2));

        verify(personService, times(1)).findById(1);
        verify(personService, times(1)).findCurrentHistories(person1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void personByIdFailTest1() throws Exception {
        when(personService.findById(10)).thenThrow(PersonException.class);
        when(personService.findCurrentHistories(person1)).thenReturn(medicalHistories);
        this.mockMvc.perform(get("/person/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(personService, times(1)).findById(10);
        verify(personService, times(0)).findCurrentHistories(person1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void personByIdFailTest2() throws Exception {
        when(personService.findById(10)).thenThrow(PersonException.class);
        when(personService.findCurrentHistories(person1)).thenReturn(medicalHistories);
        this.mockMvc.perform(get("/person/10"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addNewPersonTest() throws Exception {
        var person = new PersonDto();
        this.mockMvc.perform(get("/addNewPerson"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(model().attribute(PERSON_FOR_MODEL, person))
                .andExpect(view().name("person/person-add-info"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void addNewPersonFailTest() throws Exception {
        this.mockMvc.perform(get("/addNewPerson"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void addNewPersonForHistoryTest() throws Exception {
        var person = new PersonDto();
        this.mockMvc.perform(get("/addNewPersonForHistory"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(model().attribute(PERSON_FOR_MODEL, person))
                .andExpect(view().name("person/person-add-for-history"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void addNewPersonForHistoryFailTest() throws Exception {
        this.mockMvc.perform(get("/addNewPersonForHistory"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void saveNewPersonTest() throws Exception {
        personDto.setKeyForUser(null);
        personDto.setPhoneNumber("375297342979");
        this.mockMvc.perform(post("/saveNewPerson")
                        .flashAttr("person",personDto))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/person/0"));

        verify(personService, times(1)).createPersonFromPersonDtoAndSave(personDto);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void saveNewPersonFailTest1() throws Exception {
        this.mockMvc.perform(post("/saveNewPerson")
                        .param("firstName", "a")
                        .param("surname", "b")
                        .param("patronymic", "q")
                        .param("phoneNumber", "1")
                        .param("address", "")
                        .param("dateOfBirthday", LocalDate.now().plusDays(10).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(model().errorCount(7))
                .andExpect(view().name("person/person-add-info"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void saveNewPersonFailTest2() throws Exception {
        this.mockMvc.perform(post("/saveNewPerson")
                        .param("firstName", personDto.getFirstName())
                        .param("surname", personDto.getSurname())
                        .param("patronymic", personDto.getPatronymic())
                        .param("phoneNumber", personDto.getPhoneNumber())
                        .param("address", personDto.getAddress())
                        .param("dateOfBirthday", LocalDate.now().minusDays(10).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void infoUpdatePersonTest() throws Exception {
        when(personService.createPersonDtoFromPerson(1)).thenReturn(personDto);
        this.mockMvc.perform(get("/updatePerson/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", personDto))
                .andExpect(status().isOk())
                .andExpect(view().name("person/person-add-info"));

        verify(personService, times(1)).createPersonDtoFromPerson(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void infoUpdatePersonFailTest1() throws Exception {
        when(personService.createPersonDtoFromPerson(1)).thenReturn(personDto);
        this.mockMvc.perform(get("/updatePerson/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService, times(0)).createPersonDtoFromPerson(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void infoUpdatePersonFailTest2() throws Exception {
        when(personService.createPersonDtoFromPerson(20)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/updatePerson/20"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService, times(1)).createPersonDtoFromPerson(20);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR", "ADMIN"})
    void deletePersonTest() throws Exception {
        this.mockMvc.perform(get("/deletePerson/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/1?sortField=surname&sortDirection=asc"));
        verify(personService, times(1)).deleteById(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR"})
    void deletePersonFailTest1() throws Exception {
        this.mockMvc.perform(get("/deletePerson/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService, times(0)).deleteById(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE", "DOCTOR", "ADMIN"})
    void deletePersonFailTest2() throws Exception {
        when(personService.deleteById(20)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/deletePerson/20"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService, times(1)).deleteById(20);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void searchPersonTest() throws Exception {
        personList.remove(person2);
        var keyword = "artem";
        when(personService.search(keyword)).thenReturn(personList);
        this.mockMvc.perform(get("/searchPerson")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("result", personList))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(view().name("person/search-person"))
                .andExpect(xpath("//*[@id='keyword']")
                        .string(keyword))
                .andExpect(xpath("//*[@id='personsTbody']/div").nodeCount(1));

        verify(personService, times(1)).search(keyword);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void searchPersonWithWrongKeywordTest() throws Exception {
        personList.remove(person2);
        var keyword = "artem";
        when(personService.search(keyword)).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/searchPerson")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("result", new ArrayList<>()))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(view().name("person/search-person"))
                .andExpect(xpath("//*[@id='keyword']")
                        .string(keyword));

        verify(personService, times(1)).search(keyword);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT", "NURSE"})
    void searchPersonWithBlankKeywordTest() throws Exception {
        var keyword = "";
        this.mockMvc.perform(get("/searchPerson")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/persons/1?sortField=surname&sortDirection=asc"));

        verify(personService, times(0)).search(keyword);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void searchPersonWithFailTest() throws Exception {
        var keyword = "";
        this.mockMvc.perform(get("/searchPerson")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));

        verify(personService, times(0)).search(keyword);
    }

    @Test
    void searchPersonWithoutUserTest() throws Exception {
        var keyword = "";
        this.mockMvc.perform(get("/searchPerson")
                        .param("keyword", keyword))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name(LOGIN_PAGE));

    }

    @Test
    void searchPersonalTest() throws Exception {
        personList.remove(person2);
        var keyword = "artem";
        when(personService.searchAndFilterPersons(keyword)).thenReturn(personList);
        this.mockMvc.perform(get("/searchPersonal").param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("result", personList))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(view().name("/person/search-personal"))
                .andExpect(xpath("//*[@id='personalKeyword']")
                        .string(keyword))
                .andExpect(xpath("//*[@id='personalTBody']/div").nodeCount(1));

        verify(personService, times(1)).searchAndFilterPersons(keyword);
    }

    @Test
    void searchPersonalWrongKeywordTest() throws Exception {
        personList.remove(person2);
        var keyword = "artem";
        when(personService.searchAndFilterPersons(keyword)).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/searchPersonal").param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("result", new ArrayList<>()))
                .andExpect(model().attribute("keyword", keyword))
                .andExpect(view().name("/person/search-personal"))
                .andExpect(xpath("//*[@id='personalKeyword']")
                        .string(keyword));

        verify(personService, times(1)).searchAndFilterPersons(keyword);
    }

    @Test
    void searchPersonalBlankKeywordTest() throws Exception {
        personList.remove(person2);
        var keyword = "";
        this.mockMvc.perform(get("/searchPersonal").param("keyword", keyword))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/searchPersonal/1"));

        verify(personService, times(0)).searchAndFilterPersons(keyword);
    }

    @Test
    void getPersonalPageTest() throws Exception {
        var pageNumber = 1;
        when(personService.getPageOfPersonWithRoleId(3, pageNumber)).thenReturn(personsPage);
        this.mockMvc.perform(get("/searchPersonal/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(3))
                .andExpect(model().attribute("page", personsPage))
                .andExpect(model().attribute("persons", personsPage.getContent()))
                .andExpect(model().attribute("pageNumber", pageNumber))
                .andExpect(view().name("person/personal-list"))
                .andExpect(xpath("//*[@id='personalListTbody']/div").nodeCount(2));

        verify(personService, times(1)).getPageOfPersonWithRoleId(3, pageNumber);
    }

    @Test
    void getPersonalPageFailTest() throws Exception {
        var pageNumber = 20;
        when(personService.getPageOfPersonWithRoleId(3, pageNumber)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/searchPersonal/20"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/searchPersonal/1"));
        verify(personService, times(1)).getPageOfPersonWithRoleId(3, pageNumber);
    }


    @Test
    void getPersonalTest() throws Exception {
        when(personService.checkAndFindPersonal(1)).thenReturn(person1);
        this.mockMvc.perform(get("/personal/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(model().attribute("person", person1))
                .andExpect(view().name("person/person-info"));
        verify(personService, times(1)).checkAndFindPersonal(1);
    }

    @Test
    void getPersonalFailTest() throws Exception {
        when(personService.checkAndFindPersonal(10)).thenThrow(PersonException.class);
        this.mockMvc.perform(get("/personal/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(view().name(ERROR_EXCEPTION_PAGE));
        verify(personService, times(1)).checkAndFindPersonal(10);
    }
}