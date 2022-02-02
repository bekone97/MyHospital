package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.PersonRepository;
import com.itacademy.myhospital.model.repository.UserRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import static com.itacademy.myhospital.constants.Constants.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
class PersonServiceImplTest {
    @Autowired
    private PersonService personService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PersonRepository personRepository;

    List<Person> personList;
    Person person1;
    Person person2;
    User user1;
    User user2;
    private Role role1;
    private Role role2;
    Set<Role> roles1;
    Set<Role> roles2;

    @BeforeEach
    public void setUp(){
        role1 = Role.builder()
                .id(1)
                .name("ROLE_ADMIN")
                .build();
        role2 = Role.builder()
                .id(4)
                .name("ROLE_PATIENT")
                .build();
        roles1=new HashSet<>();;
        roles1.add(role1);
        roles1.add(role2);
        roles2=new HashSet<>();
        roles2.add(role2);

         user1 = User.builder()
                .id(1)
                .username("myachin")
                .password("myachin")
                .email("Myachin@mail.ru")
                .verificationStatus(true)
                .verificationCode(null)
                .authenticationStatus(true)
                 .roles(roles1)
                .build();
        user2 = User.builder()
                .id(1)
                .username("asdadw")
                .password("asdadaw")
                .email("Adsdn@mail.ru")
                .verificationStatus(true)
                .verificationCode(null)
                .authenticationStatus(true)
                .roles(roles2)
                .build();
        personList = new ArrayList<>();
         person1 = Person.builder()
                .id(1)
                .firstName("Myachin")
                .surname("Artem")
                .patronymic("Valerevich")
                .phoneNumber("297342938")
                .address("g.Grodno")
                .keyForUser("asl,qlw,q")
                .dateOfBirthday(LocalDate.now())
                .user(user1)
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
                .user(user2)
                .build();
          personList.add(person1);
          personList.add(person2);

    }
    @Test
    void testFindAll() {
        when(personRepository.findAll()).thenReturn(personList);
        var allList=personService.findAll();
        assertEquals(2,allList.size());
        verify(personRepository,times(1)).findAll();
    }

    @Test
     void findByIdTest() throws PersonException {
        when(personRepository.findById(1)).thenReturn(Optional.of(person1));
        var person = personService.findById(1);
        assertEquals(person,person1);
    }
    @Test
    void findByIdFailTest() {
        when(personRepository.findById(2)).thenReturn(Optional.empty());
        PersonException exception=assertThrows(PersonException.class,
                ()->personService.findById(2));
        assertTrue(exception.getMessage().contains(NO_PERSON_EXCEPTION));
    }
    @Test
    void deleteByIdFailTest() {
        when(personRepository.existsById(2)).thenReturn(false);
        PersonException exception = assertThrows(PersonException.class,
                ()->personService.deleteById(2));
        assertTrue(exception.getMessage().contains(NO_PERSON_EXCEPTION));
    }

    @Test
    void findPersonByIdAndRoleIdTest() throws PersonException {
        when(personRepository.findPersonByIdAndRoleId(1,4)).thenReturn(person1);
        var person=personService.findPersonByIdAndRoleId(1,4);
        assertNotNull(person);
    }
    @Test
    void findPersonByIdAndRoleIdFailTest() {
        when(personRepository.findPersonByIdAndRoleId(1,4)).thenReturn(null);
        Exception exception=assertThrows(PersonException.class,
                ()->personService.findPersonByIdAndRoleId(1,4));
        assertTrue(exception.getMessage().contains(" and role id: "));
    }

    @Test
    void getPageOfPersonWithRoleIdTest() throws PersonException {
        int numberOfPage = 1;
        int roleId = 1;
        Pageable pageable = PageRequest.of(numberOfPage-1,2);
        when(personRepository.findNumberOfPersonWithRoleId(roleId)).thenReturn(4);
        when(personRepository.findLimitAndSortedPersonWithRoleId(1,pageable.getPageSize(),pageable.getOffset()))
                .thenReturn(personList);
        Page<Person> personPage=personService.getPageOfPersonWithRoleId(roleId,numberOfPage);
        assertEquals(personPage, new PageImpl<>(personList,pageable,4));
    }
    @Test
    void getPageOfPersonWithRoleIdFailTest(){
        int numberOfPage = 1;
        int roleId = 1;
        Pageable pageable = PageRequest.of(0,2);
        when(personRepository.findNumberOfPersonWithRoleId(roleId)).thenReturn(0);
        when(personRepository.findLimitAndSortedPersonWithRoleId(1,pageable.getPageSize(),pageable.getOffset()))
                .thenReturn(new ArrayList<>());
        Exception exception = assertThrows(PersonException.class,
                ()->personService.getPageOfPersonWithRoleId(roleId,numberOfPage));
        assertTrue(exception.getMessage().contains(NO_PERSONS_WITH_ROLE_EXCEPTION));
    }

    @Test
    void createPersonDtoFromPerson() throws PersonException {
        when(personRepository.findById(1)).thenReturn(Optional.of(person1));
        var personDto=personService.createPersonDtoFromPerson(1);
        assertEquals(personDto.getId(),person1.getId());
        assertEquals(personDto.getFirstName(),person1.getFirstName());
        assertEquals(personDto.getSurname(),person1.getSurname());
        assertEquals(personDto.getPatronymic(),person1.getPatronymic());
        assertEquals(personDto.getAddress(),person1.getAddress());
        assertEquals(LocalDate.parse(personDto.getDateOfBirthday()),person1.getDateOfBirthday());

        verify(personRepository,times(1)).findById(1);
    }
    @Test
    void createPersonDtoFromPersonFailTest()  {
        when(personRepository.findById(20)).thenReturn(Optional.empty());
    Exception exception = assertThrows(PersonException.class,
            ()->personService.createPersonDtoFromPerson(20));

        assertTrue(exception.getMessage().contains(NO_PERSON_EXCEPTION));
        verify(personRepository,times(1)).findById(20);
    }

    @Test
    void checkRequestParameterFromSelectTest() {
        when(personRepository.findById(1)).thenReturn(Optional.ofNullable(person1));
        boolean isChecked=personService.checkRequestParameterFromSelect("1");
        verify(personRepository,times(1)).findById(1);
        assertTrue(isChecked);
    }
    @Test
    void checkRequestParameterFromSelectTestOne() {
        when(personRepository.findById(1)).thenReturn(Optional.empty());
        boolean isChecked=personService.checkRequestParameterFromSelect("1");
        verify(personRepository,times(1)).findById(1);
        assertFalse(isChecked);
    }
    @Test
    void checkRequestParameterFailTestTwo() {
        boolean isChecked=personService.checkRequestParameterFromSelect("1a");
        assertFalse(isChecked);
    }
    @Test
    void searchAndFilterPersonsTest() {
        String keyword = "keyword";
        when(personRepository.search(keyword)).thenReturn(personList);
        var personal = personService.searchAndFilterPersons(keyword);
        assertEquals(1,personal.size());
        assertEquals(person1,personal.stream().findAny().get());
    }

    @Test
    void addUserToPersonTest() throws UserException, PersonException {
        person1.setUser(null);
        user1.setAuthenticationStatus(false);
        when(personRepository.findByKeyForUser(person1.getKeyForUser())).thenReturn(person1);
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        personService.addUserToPerson(person1.getKeyForUser(),user1.getUsername());
        assertEquals(person1.getUser(),user1);
        assertTrue(user1.getAuthenticationStatus());
        verify(personRepository,times(1)).findByKeyForUser(person1.getKeyForUser());
        verify(userRepository,times(1)).findUserByUsername(user1.getUsername());
        verify(personRepository,times(1)).saveAndFlush(person1);
    }
    @Test
    void addUserToPersonFailTest1() {
        user1.setAuthenticationStatus(false);
        when(personRepository.findByKeyForUser(person1.getKeyForUser())).thenReturn(person1);
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        Exception exception = assertThrows(PersonException.class,
                ()->personService.addUserToPerson(person1.getKeyForUser(),user1.getUsername()));
        assertTrue(exception.getMessage().contains(PERSON_HAS_A_USER_EXCEPTION));
    }
    @Test
    void addUserToPersonFailTest2() {
        user1.setAuthenticationStatus(false);
        person1.setUser(user1);
        when(personRepository.findByKeyForUser("kakaka")).thenReturn(person1);
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        Exception exception = assertThrows(PersonException.class,
                ()->personService.addUserToPerson("kakaka",user1.getUsername()));
        assertTrue(exception.getMessage().contains(PERSON_HAS_A_USER_EXCEPTION));
    }
    @Test
    void addUserToPersonFailTest3() {
        when(personRepository.findByKeyForUser(person1.getKeyForUser())).thenReturn(person1);
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(user1);
        Exception exception = assertThrows(UserException.class,
                ()->personService.addUserToPerson(person1.getKeyForUser(),user1.getUsername()));
        assertTrue(exception.getMessage().contains(USER_HAS_A_PERSON_EXCEPTION));
    }
    @Test
    void addUserToPersonFailTest4() {
        when(personRepository.findByKeyForUser(person1.getKeyForUser())).thenReturn(person1);
        when(userRepository.findUserByUsername(user1.getUsername())).thenReturn(null);
        Exception exception = assertThrows(UserException.class,
                ()->personService.addUserToPerson(person1.getKeyForUser(),user1.getUsername()));
        assertTrue(exception.getMessage().contains(USER_HAS_A_PERSON_EXCEPTION));
    }

    @Test
    void checkAndFindPersonalFailTest() {
        when(personRepository.findPersonByIdAndRoleId(1,3)).thenReturn(null);
        Exception exception = assertThrows(PersonException.class,
                ()->personService.checkAndFindPersonal(1));
        assertTrue(exception.getMessage().contains("and role id: "));
    }

    @Test
    void getCurrentHistories() {
       var current= MedicalHistory.builder()
                .dischargeStatus(false)
                .build();
       var notCurrent= MedicalHistory.builder()
                .dischargeStatus(true)
                .build();
       List<MedicalHistory> histories = new ArrayList<>();
       histories.add(current);
       histories.add(notCurrent);
       person1.setHistories(histories);
       var currentHistories=personService.findCurrentHistories(person1);
       assertEquals(1,currentHistories.size());
       assertFalse(currentHistories.stream().findAny().get().isDischargeStatus());
    }
}