package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.MedicalHistory;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.PersonRepository;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.UserService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    public static final int NURSE_ROLE_ID = 3;
    public static final String NO_PERSON_EXCEPTION = "The person doesn't exist with id : ";
    public static final String NO_PERSONS_WITH_ROLE_EXCEPTION = "There are no persons with the role: ";


    private final PersonRepository personRepository;
    private final UUIDService uuidService;
    private final UserService userService;

    public PersonServiceImpl(PersonRepository personRepository, UUIDService uuidService, UserService userService) {
        this.personRepository = personRepository;
        this.uuidService = uuidService;
        this.userService = userService;
    }

    @Override
    public Page<Person> findAll(int pageNumber, String sortField, String sortDirection) throws PersonException {
        Sort sort = Sort.by(sortField);
        sort=sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber-1,2,sort);
        var personPage= personRepository.findAll(pageable);
        if (personPage.getContent().isEmpty()){
            throw new PersonException("There is no page with this number : "+pageNumber);
        }
        return personPage;
    }

    @Override
    public List<Person> findAll() {
       return personRepository.findAll();
    }

    @Override
    public Person findById(Integer id) throws PersonException {
        Person person;
        var optional = personRepository.findById(id);
        if(optional.isPresent()) {
            person = optional.get();
        }else {
            throw new PersonException(NO_PERSON_EXCEPTION);
        }
        return person;
    }

    @Override
    public void saveAndFlush(Person person) {
        personRepository.saveAndFlush(person);
    }

    @Override
    public boolean deleteById(Integer id) throws PersonException {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return true;
        }else {
            throw new PersonException(NO_PERSON_EXCEPTION);
        }
    }
    @Override
    public Person findByUser(User user) {
        return personRepository.findByUser(user);
    }

    @Override
    public List<Person> search(String keyword){
       return personRepository.search(keyword);
    }
    public Person findPersonByKey(String key) throws PersonException {
        var person = findByKeyForUser(key);
        if (person==null){
            throw new PersonException(NO_PERSON_EXCEPTION+ "with this key : "+key);
        }
        return person;
    }
    public Person findByKeyForUser(String key) {
        return personRepository.findByKeyForUser(key);
    }

    @Override
    public Person findPersonByUsernameOfUser(String username)  {
        return personRepository.findPersonByUsernameOfUser(username);
    }

    @Override
    public List<Person> findLimitAndSortedPersonWithRoleId(Integer roleId, long limit,long offset)  {
            return personRepository.findLimitAndSortedPersonWithRoleId(roleId,limit,offset);
    }

    @Override
    public Person findPersonByIdAndRoleId(Integer personId, Integer roleId) throws PersonException {
        var personWithRole= personRepository.findPersonByIdAndRoleId(personId,roleId);
        if (personWithRole==null){
            throw new PersonException(NO_PERSON_EXCEPTION+personId+" and role id: "+roleId);
        }
        return personWithRole;
    }
    @Override
    public Page<Person> getPageOfPersonWithRoleId(Integer roleId,int numberOfPage) throws PersonException {
        Pageable pageable = PageRequest.of(numberOfPage-1,2);
        var numberOfPerson = personRepository.findNumberOfPersonWithRoleId(roleId);
        var listOfPersonWithRoleId= findLimitAndSortedPersonWithRoleId(roleId,
                pageable.getPageSize(),pageable.getOffset());
        if (listOfPersonWithRoleId.isEmpty()) {
        throw new PersonException(NO_PERSONS_WITH_ROLE_EXCEPTION + roleId);
        }
        return new PageImpl<>(listOfPersonWithRoleId,pageable,numberOfPerson);
    }



    @Override
    public void createPersonFromPersonDtoAndSave(PersonDto personDto){
        String keyForUser = createAndCheckKey();
        Person person = Person.builder()
                .id(personDto.getId())
                .firstName(personDto.getFirstName())
                .surname(personDto.getSurname())
                .patronymic(personDto.getPatronymic())
                .address(personDto.getAddress())
                .dateOfBirthday(LocalDate.parse(personDto.getDateOfBirthday()))
                .phoneNumber(personDto.getPhoneNumber())
                .keyForUser(keyForUser)
                .build();
       saveAndFlush(person);
    }

//    Create personDto for update
    @Override
    public PersonDto createPersonDtoFromPerson(Integer id) throws PersonException {
        var person=findById(id);
        return PersonDto.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .surname(person.getSurname())
                .patronymic(person.getPatronymic())
                .dateOfBirthday(person.getDateOfBirthday().toString())
                .address(person.getAddress())
                .user(person.getUser())
                .keyForUser(person.getKeyForUser())
                .build();
    }
//check person Does he equal null or not
    @Override
    public boolean checkRequestParameterFromSelect(String id) {
        if (isDigit(id)) {
            Person person;
            try {
                person = findById(Integer.parseInt(id));
                return person != null;
            } catch (PersonException e) {
                return false;
            }
        }else {
            return false;
        }
    }

    //Return persons by keyword and filter the with roleId <4 (NURSE DOCTOR AND ADMIN)
    @Override
    public List<Person> searchAndFilterPersons(String keyword){
        var resultPersons = search(keyword);
        resultPersons = resultPersons.stream().filter(person -> person.getUser().getRoles().stream()
                        .anyMatch(role -> role.getId() < 4))
                .collect(Collectors.toList());
        return resultPersons;
    }

    @Override
    @Transactional
    public Person addUserToPerson(String key, String username) throws UserException, PersonException {
        var person = findByKeyForUser(key);
        var user = userService.findByUsername(username);
        if (user!=null&&!user.getAuthenticationStatus()) {
            if (person!=null&&person.getUser()==null) {
                person.setUser(user);
                user.setAuthenticationStatus(true);
                saveAndFlush(person);
                return person;
            }else {
                throw new PersonException("Person also has a user or no person with this key");
            }
        }else {
            throw new UserException("User also has a person");
        }
    }

    @Override
    public Person checkAndFindPersonal(Integer id) throws PersonException {
        var personal= findPersonByIdAndRoleId(id, NURSE_ROLE_ID);
        return personal;
    }

    @Override
    public List<Person> findPersonsByRoleId(Integer roleId){
        return personRepository.findPersonsByRoleId(roleId);

    }

    //Check string - is it number or not ?
    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    //Check key on unique value
    private String createAndCheckKey() {
        String key = uuidService.getRandomString();
            if(findByKeyForUser(key)!=null)
            key= createAndCheckKey();
        return key;
    }


    public List<MedicalHistory> getCurrentHistories (Person person){
        return person.getHistories().stream()
                .filter(medicalHistory -> !medicalHistory.isStatus())
                .collect(Collectors.toList());
    }
}
