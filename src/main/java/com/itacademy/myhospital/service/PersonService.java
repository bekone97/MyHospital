package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonService  {

    Page<Person> findAll(int pageNumber, String sortField, String sortDirection) throws PersonException;
    List<Person> findAll();
    Person findById(Integer id) throws PersonException;
    void saveAndFlush(Person person);
    boolean deleteById(Integer id) throws PersonException;

    Person findByUser(User user);
    List<Person> search(String keyword);
    Person findByKeyForUser(String key) throws PersonException;
    Person findPersonByUsernameOfUser(String username);
    List<Person> findLimitAndSortedPersonWithRoleId(Integer roleId,long limit ,long offset);
    /**
     * The method returns persons page with required role
     * @param roleId - with what role returns page of person
     * @param numberOfPage - what number of page
     * @return the page of persons who have the roleId
     * @throws PersonException if there is no page with the number
     */
    Page<Person> getPageOfPersonWithRoleId(Integer roleId,int numberOfPage) throws PersonException;
    Person findPersonByIdAndRoleId(Integer personId, Integer roleId) throws PersonException;

    void createPersonFromPersonDtoAndSave(PersonDto personDto);
    /**
     * This method is for creating a personDto from a person with the id.
     * @param id - id of person
     * @return a PersonDto
     * @throws PersonException if there is no person with the id
     */
    PersonDto createPersonDtoFromPerson(Integer id) throws PersonException;
    boolean checkRequestParameterFromSelect(String id);
    /**
     * This method is for searching a person with roles : ROLE_NURSE, ROLE_DOCTOR, ROLE_ADMIN
     * @param keyword - The string used to search for a person
     * @return a list of persons
     */
    List<Person> searchAndFilterPersons(String keyword) ;
    /**
     * This method finds a person by  the key and a user by the username, and checks the person and the user for adding
     * them to each other.
     * @param key - a string for finding a person
     * @param username - name of user
     * @return a person if the method completed successfully
     * @throws UserException if there is no user with this username, or authentication stats is true
     * @throws PersonException if there is no person with the key, or it already has a user
     */
    Person addUserToPerson(String key, String username) throws UserException, PersonException;
    Person checkAndFindPersonal(Integer id) throws PersonException;
    List<Person> findPersonsByRoleId(Integer roleId);
    /**
     * This method finds medical histories of the person and filter them by a discharge status of the medical history
     * @param person - whose medical histories
     * @return a list of current medical histories
     */
    List<MedicalHistory> findCurrentHistories(Person person);
}
