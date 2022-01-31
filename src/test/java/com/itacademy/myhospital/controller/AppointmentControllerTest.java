package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.AppointmentDto;
import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.UserService;
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
class AppointmentControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;
    @MockBean
    private PersonService personService;
    @MockBean
    private UserService userService;
    @MockBean
    private Principal principal;
    private User user1;
    List<Person> personList;
    private Person person1;
    private Person person2;
    List<LocalDate> dates;
    private Person patient;
    private Appointment appointment1;
    private Appointment appointment2;
    private Appointment appointment3;
    private List<Appointment> appointments;
    private AppointmentDto appointmentDto;

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
                .user(null)
                .build();
        personList.add(person1);
        personList.add(person2);
        dates=new ArrayList<>();
        dates.add(LocalDate.now());
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.now().plusDays(2));
        patient = Person.builder()
                .id(3)
                .firstName("Asda")
                .surname("saqdw")
                .patronymic("sadqwq")
                .phoneNumber("123214")
                .build();
        appointment1 = Appointment.builder()
                .dateOfAppointment(Timestamp.valueOf(LocalDateTime.now().minusMinutes(15)))
                .id(1)
                .personal(person1)
                .build();
        appointment2 = Appointment.builder()
                .dateOfAppointment(Timestamp.valueOf(LocalDateTime.now()))
                .id(2)
                .personal(person1)
                .build();
        appointment3 = Appointment.builder()
                .dateOfAppointment(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)))
                .id(3)
                .personal(person1)
                .isEngaged(true)
                .userPatient(user1)
                .build();
        appointmentDto= AppointmentDto.builder()
                .phoneNumber("123412")
                .personal(person1)
                .dateOfAppointment(LocalDate.now().toString())
                .build();
        appointments = new ArrayList<>();
        appointments.add(appointment1);
        appointments.add(appointment2);
        appointments.add(appointment3);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeChoiceOfDateAndDoctorUserWithPersonTest() throws Exception {
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        when(principal.getName()).thenReturn(user1.getUsername());
        when(personService.findPersonsByRoleId(2)).thenReturn(personList);
        when(personService.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(patient);
        this.mockMvc.perform(get("/choiceOfDateAndDoctor"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(model().attribute("patient", patient))
                .andExpect(model().attribute("listOfDates", dates))
                .andExpect(model().attribute("listOfDoctors", personList))
                .andExpect(model().attribute("appointmentDto", AppointmentDto.builder()
                        .phoneNumber(patient.getPhoneNumber())
                        .build()))
                .andExpect(view().name("appointment/choiceOfDateAndPersonal"))
                .andExpect(xpath("//*[@id='doctors']/div").nodeCount(2))
                .andExpect(xpath("//*[@id='date']/div").nodeCount(3));
        verify(appointmentService, times(1)).createListOfDays(LocalDate.now());
        verify(personService, times(1)).findPersonsByRoleId(2);
        verify(personService, times(1)).findPersonByUsernameOfUser(user1.getUsername());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeChoiceOfDateAndDoctorUserWithoutPersonTest() throws Exception {
        List<LocalDate> dates = new ArrayList<>();
        dates.add(LocalDate.now());
        dates.add(LocalDate.now().plusDays(1));
        dates.add(LocalDate.now().plusDays(2));

        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        when(personService.findPersonsByRoleId(2)).thenReturn(personList);
        when(personService.findPersonByUsernameOfUser(user1.getUsername())).thenReturn(null);
        this.mockMvc.perform(get("/choiceOfDateAndDoctor"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(3))
                .andExpect(model().attribute("listOfDates", dates))
                .andExpect(model().attribute("listOfDoctors", personList))
                .andExpect(model().attribute("appointmentDto", new AppointmentDto()))
                .andExpect(view().name("appointment/choiceOfDateAndPersonal"))
                .andExpect(xpath("//*[@id='doctors']/div").nodeCount(2))
                .andExpect(xpath("//*[@id='date']/div").nodeCount(3))
                .andExpect(xpath("//*[@id='phoneNumber']").nodeCount(1));
        verify(appointmentService, times(1)).createListOfDays(LocalDate.now());
        verify(personService, times(1)).findPersonsByRoleId(2);
        verify(personService, times(1)).findPersonByUsernameOfUser(user1.getUsername());
    }
    @Test
    void makeChoiceOfDateAndDoctorWithoutUserTest() throws Exception {

        this.mockMvc.perform(get("/choiceOfDateAndDoctor"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }


    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeChoiceTimeOfDayTest() throws Exception {

        when(appointmentService.findAppointmentsOfDoctorOnDay(LocalDate.now().toString(),person1)).thenReturn(appointments);
        this.mockMvc.perform(get("/choiceOfTime")
                .flashAttr("appointmentDto",appointmentDto))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(5))
                .andExpect(model().attribute("appointments",appointments))
                .andExpect(model().attribute("date",appointmentDto.getDateOfAppointment()))
                .andExpect(model().attribute("phoneNumber",appointmentDto.getPhoneNumber()))
                .andExpect(view().name("appointment/choice-time-of-appointment"))
                .andExpect(xpath("//*[@id='buttonsOfTime']/div").nodeCount(3));
        verify(appointmentService,times(1)).findAppointmentsOfDoctorOnDay(LocalDate.now().toString(),person1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeChoiceTimeOfDayWithWrongValid() throws Exception {
      appointmentDto.setPhoneNumber("");
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        when(personService.findPersonsByRoleId(2)).thenReturn(personList);
        this.mockMvc.perform(get("/choiceOfTime")
                        .flashAttr("appointmentDto",appointmentDto))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(3))
                .andExpect(model().attribute("listOfDoctors",personList))
                .andExpect(model().attribute("listOfDates",dates))
                .andExpect(view().name("appointment/choiceOfDateAndPersonal"));

        verify(appointmentService,times(1)).createListOfDays(LocalDate.now());
        verify(personService,times(1)).findPersonsByRoleId(2);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeAppointmentTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.changeAppointmentOnBlockedValue(patient.getPhoneNumber(), user1.getUsername(), 1))
                .thenReturn(true);
        this.mockMvc.perform(post("/makeAppointment/1")
                .param("phoneNumber",patient.getPhoneNumber()))
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCurrentAppointments"));

        verify(appointmentService,times(1))
                .changeAppointmentOnBlockedValue(patient.getPhoneNumber(),user1.getUsername(), 1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void makeAppointmentFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.changeAppointmentOnBlockedValue(patient.getPhoneNumber(), user1.getUsername(), 1))
                .thenThrow(AppointmentException.class);
        this.mockMvc.perform(post("/makeAppointment/1")
                        .param("phoneNumber",patient.getPhoneNumber()))
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));

        verify(appointmentService,times(1))
                .changeAppointmentOnBlockedValue(patient.getPhoneNumber(),user1.getUsername(), 1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void myAppointments() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(userService.findByUsername(user1.getUsername())).thenReturn(user1);

        this.mockMvc.perform(get("/myCurrentAppointments"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(3))
                .andExpect(model().attribute("user",user1))
                .andExpect(view().name("appointment/user-appointments"));

        verify(userService,times(1)).findByUsername(user1.getUsername());

    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void myAllAppointmentsTest() throws Exception {
        appointment1.setUserPatient(user1);
        appointment2.setUserPatient(user1);
        appointment3.setUserPatient(user1);
        when(principal.getName()).thenReturn(user1.getUsername());
        when(userService.findByUsername(user1.getUsername())).thenReturn(user1);
        when(appointmentService.findByUserPatient(user1)).thenReturn(appointments);
        this.mockMvc.perform(get("/myAllAppointments"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(3))
                .andExpect(model().attribute("user",user1))
                .andExpect(model().attribute("appointments",appointments))
                .andExpect(view().name("appointment/user-appointments"))
                .andExpect(xpath("//*[@id='userAppointmentsTbody']/div").nodeCount(3));
        verify(userService,times(1)).findByUsername(user1.getUsername());
        verify(appointmentService,times(1)).findByUserPatient(user1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void allAppointmentsOfUserTest() throws Exception {
        appointment1.setUserPatient(user1);
        appointment2.setUserPatient(user1);
        appointment3.setUserPatient(user1);
        when(userService.findById(1)).thenReturn(user1);
        when(appointmentService.findByUserPatient(user1)).thenReturn(appointments);
        this.mockMvc.perform(get("/allAppointmentsOfUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("user",user1))
                .andExpect(model().attribute("appointments",appointments))
                .andExpect(view().name("appointment/user-appointments-for-doctor"));

        verify(userService,times(1)).findById(1);
        verify(appointmentService,times(1)).findByUserPatient(user1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void allAppointmentsOfUserFailTest1() throws Exception {
        when(userService.findById(1)).thenThrow(UserException.class);
        this.mockMvc.perform(get("/allAppointmentsOfUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));

        verify(userService,times(1)).findById(1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void allAppointmentsOfUserWithUserWithoutRoleDoctor() throws Exception {
        when(userService.findById(1)).thenThrow(UserException.class);
        this.mockMvc.perform(get("/allAppointmentsOfUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());

        verify(userService,times(0)).findById(1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void cancelAppointmentByUserTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.cancelAppointmentByUser(user1.getUsername(),1)).thenReturn(true);
        this.mockMvc.perform(post("/cancelAppointmentByUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCurrentAppointments"));
        verify(appointmentService,times(1)).cancelAppointmentByUser(user1.getUsername(),1);
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT"})
    void cancelAppointmentByUserFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.cancelAppointmentByUser(user1.getUsername(),1)).thenThrow(AppointmentException.class);
        this.mockMvc.perform(post("/cancelAppointmentByUser/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).cancelAppointmentByUser(user1.getUsername(),1);
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void cancelAppointmentByDoctorTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.cancelAppointmentByDoctor(1,user1.getUsername())).thenReturn(true);
        this.mockMvc.perform(post("/cancelAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCurrentAppointments"));
        verify(appointmentService,times(1)).cancelAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void cancelAppointmentByDoctorFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.cancelAppointmentByDoctor(1,user1.getUsername())).thenThrow(UserException.class);
        this.mockMvc.perform(post("/cancelAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).cancelAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void cancelAppointmentByDoctorFailTest2() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.cancelAppointmentByDoctor(1,user1.getUsername())).thenReturn(true);
        this.mockMvc.perform(post("/cancelAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(authenticated())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void getMyAppointmentsScheduleTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.getAppointmentsOfDoctorByDate(LocalDate.now(),user1.getUsername()))
                .thenReturn(appointments);
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        this.mockMvc.perform(get("/mySchedule"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(model().attribute("appointments",appointments))
                .andExpect(model().attribute("dates",dates))
                .andExpect(model().attribute("currentDate",LocalDate.now()))
                .andExpect(view().name("appointment/appointments-schedule"));
        verify(appointmentService,times(1)).getAppointmentsOfDoctorByDate(LocalDate.now(),user1.getUsername());
        verify(appointmentService,times(1)).createListOfDays(LocalDate.now());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void getMyAppointmentsScheduleFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.getAppointmentsOfDoctorByDate(LocalDate.now(),user1.getUsername()))
                .thenThrow(PersonException.class);
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        this.mockMvc.perform(get("/mySchedule"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).getAppointmentsOfDoctorByDate(LocalDate.now(),user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void getMyAppointmentsScheduleFailTest2() throws Exception {
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        this.mockMvc.perform(get("/mySchedule"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void getMyAppointmentsScheduleByDateTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.getAppointmentsOfDoctorByDate(LocalDate.now().plusDays(2),user1.getUsername()))
                .thenReturn(appointments);
        when(appointmentService.createListOfDays(LocalDate.now())).thenReturn(dates);
        this.mockMvc.perform(get("/myScheduleByDate")
                        .param("date",LocalDate.now().plusDays(2).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(model().attribute("appointments",appointments))
                .andExpect(model().attribute("dates",dates))
                .andExpect(model().attribute("currentDate",LocalDate.now().plusDays(2)))
                .andExpect(view().name("appointment/appointments-schedule"));
        verify(appointmentService,times(1)).getAppointmentsOfDoctorByDate(LocalDate.now().plusDays(2),
                user1.getUsername());
        verify(appointmentService,times(1)).createListOfDays(LocalDate.now());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void getMyAppointmentsScheduleByDateFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.getAppointmentsOfDoctorByDate(LocalDate.now().plusDays(2),user1.getUsername()))
                .thenThrow(PersonException.class);
        this.mockMvc.perform(get("/myScheduleByDate")
                        .param("date",LocalDate.now().plusDays(2).toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).getAppointmentsOfDoctorByDate(LocalDate.now().plusDays(2),
                user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void getMyAppointmentsScheduleByDateFailTest2() throws Exception {
        this.mockMvc.perform(get("/myScheduleByDate"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void blockAppointmentByDoctorTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.blockAppointmentByDoctor(1,user1.getUsername())).thenReturn(true);
        this.mockMvc.perform(post("/blockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mySchedule"));
        verify(appointmentService,times(1)).blockAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void blockAppointmentByDoctorFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.blockAppointmentByDoctor(1,user1.getUsername())).thenThrow(AppointmentException.class);
        this.mockMvc.perform(post("/blockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).blockAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void blockAppointmentByDoctorFailTest2() throws Exception {
        this.mockMvc.perform(post("/blockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void unblockAppointmentByDoctorTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.unblockAppointmentByDoctor(1,user1.getUsername())).thenReturn(true);
        this.mockMvc.perform(post("/unblockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mySchedule"));
        verify(appointmentService,times(1)).unblockAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE","DOCTOR"})
    void unblockAppointmentByDoctorFailTest() throws Exception {
        when(principal.getName()).thenReturn(user1.getUsername());
        when(appointmentService.unblockAppointmentByDoctor(1,user1.getUsername())).thenThrow(AppointmentException.class);
        this.mockMvc.perform(post("/unblockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().size(1))
                .andExpect(view().name("error/exception"));
        verify(appointmentService,times(1)).unblockAppointmentByDoctor(1,user1.getUsername());
    }
    @Test
    @WithMockUser(username = "user", roles = {"PATIENT","NURSE"})
    void unblockAppointmentByDoctorFailTest2() throws Exception {
        this.mockMvc.perform(post("/unblockAppointmentByDoctor/1"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}