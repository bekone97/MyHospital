package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PersonService  {

    Page<Person> findAll(int pageNumber, String sortField, String sortDirection) throws PersonException;
    List<Person> findAll();
    Person findById(Integer id) throws PersonException;
    void saveAndFlush(Person person);
    boolean deleteById(Integer id) throws PersonException;
    Person findPersonByKey(String key) throws PersonException;
    Person findByUser(User user);
    List<Person> search(String keyword);
    Person findByKeyForUser(String key) throws PersonException;
    Person findPersonByUsernameOfUser(String username);
    List<Person> findLimitAndSortedPersonWithRoleId(Integer roleId,long limit ,long offset);
    Page<Person> getPageOfPersonWithRoleId(Integer roleId,int numberOfPage) throws PersonException;
    public Person findPersonByIdAndRoleId(Integer personId, Integer roleId) throws PersonException;
    public void createPersonFromPersonDtoAndSave(PersonDto personDto);
    public PersonDto createPersonDtoFromPerson(Integer id) throws PersonException;
    public boolean checkRequestParameterFromSelect(String id);
    public List<Person> searchAndFilterPersons(String keyword) ;
    public Person addUserToPerson(String key, String username) throws UserException, PersonException;
    public Person checkAndFindPersonal(Integer id) throws PersonException;
    public List<Person> findPersonsByRoleId(Integer roleId);
    public List<MedicalHistory> getCurrentHistories (Person person);
}
