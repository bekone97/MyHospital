package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.NameOfProcessesException;
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
import static com.itacademy.myhospital.constants.Constants.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {




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
        sort=sortDirection.equals(ASC_FOR_SORT_DIRECTION) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber-1,2,sort);
        var personPage= personRepository.findAll(pageable);
        if (personPage.getContent().isEmpty()){
            throw new PersonException(NO_PAGE_WITH_NUMBER_EXCEPTION +pageNumber);
        }
        return personPage;
    }

    @Override
    public List<Person> findAll() {
       return personRepository.findAll();
    }

    @Override
    public Person findById(Integer id) throws PersonException {
        return personRepository.findById(id)
                .orElseThrow(()->new PersonException(NO_PERSON_EXCEPTION));
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
    @Transactional
    public void createPersonFromPersonDtoAndSave(PersonDto personDto){
        String keyForUser = createKey();
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
                        .anyMatch(role -> role.getId() < ROLE_PATIENT_ID))
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
                throw new PersonException(PERSON_HAS_A_USER_EXCEPTION + "or" +NO_PERSON_WITH_KEY_EXCEPTION);
            }
        }else {
            throw new UserException(USER_HAS_A_PERSON_EXCEPTION);
        }
    }

    @Override
    public Person checkAndFindPersonal(Integer id) throws PersonException {
        return findPersonByIdAndRoleId(id, ROLE_NURSE_ID);
    }

    @Override
    public List<Person> findPersonsByRoleId(Integer roleId){
        return personRepository.findPersonsByRoleId(roleId);

    }

    /**
     * This method checks the string - is it only numbers or not ?
     * @param s - incoming string
     * @return true if the string has only numbers, and false - if not
     */
    private static boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private String createKey() {
        return uuidService.getRandomString();
    }



    public List<MedicalHistory> findCurrentHistories(Person person){
        return person.getHistories().stream()
                .filter(medicalHistory -> !medicalHistory.isDischargeStatus())
                .collect(Collectors.toList());
    }
}
