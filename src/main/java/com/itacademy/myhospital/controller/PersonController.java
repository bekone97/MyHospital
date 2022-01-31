package com.itacademy.myhospital.controller;//package com.academy.controller;

import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.validator.PersonAgeValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import static com.itacademy.myhospital.constants.Constants.*;
import javax.validation.Valid;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class PersonController {
    public static final String REDIRECT_PERSONS_PAGE = "redirect:/persons/1?sortField=surname&sortDirection=asc";

    public static final String PERSON_PERSON_ADD_INFO_VIEW = "person/person-add-info";
    public static final String PERSON_PERSON_ADD_FOR_HISTORY_VIEW = "person/person-add-for-history";
    public static final String PERSON_PATIENTS_VIEW = "person/patients";
    public static final String PERSON_SEARCH_PERSON_VIEW = "person/search-person";
    public static final String PERSON_SEARCH_PERSONAL_VIEW = "/person/search-personal";
    public static final String PERSON_PERSON_INFO_VIEW = "person/person-info";
    public static final String REDIRECT_PERSONS_VIEW = "redirect:/persons/1?sortField=surname&sortDirection=asc";


    private final PersonService personService;
    private final PersonAgeValidatorService personAgeValidatorService;

    @PreAuthorize("hasAuthority('ROLE_NURSE')")
    @GetMapping(value = "/persons/{pageNumber}")
    public String persons(@PathVariable("pageNumber") int pageNumber,
                          @RequestParam("sortField") String sortField,
                          @RequestParam("sortDirection") String sortDirection,
                          Model model) {
        try {
            Page<Person> personsPage = personService.findAll(pageNumber, sortField, sortDirection);
            var listOfPerson = personsPage.getContent();
            var reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                    DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
            model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
            model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
            model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);
            model.addAttribute(PAGE_FOR_MODEL, personsPage);
            model.addAttribute(PERSONS_FOR_MODEL, listOfPerson);
            return "person/persons";
        } catch (PersonException e) {
            return REDIRECT_PERSONS_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping(value = "person/{id}")
    public String personById(@PathVariable Integer id, Model model) throws PersonException {

            Person person = personService.findById(id);
            var currentHistories=personService.findCurrentHistories(person);
            model.addAttribute(HISTORIES_FOR_MODEL,currentHistories);
            model.addAttribute(PERSON_FOR_MODEL, person);
            return PERSON_PERSON_INFO_VIEW;

    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/addNewPerson")
    public String addNewPerson(Model model) {
        var person = new PersonDto();
        model.addAttribute(PERSON_FOR_MODEL, person);
        return PERSON_PERSON_ADD_INFO_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/addNewPersonForHistory")
    public String addNewPersonForHistory(Model model) {
        PersonDto personDto = new PersonDto();
        model.addAttribute(PERSON_FOR_MODEL, personDto);
        return PERSON_PERSON_ADD_FOR_HISTORY_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/saveNewPerson")
    public String saveNewPerson(@Valid @ModelAttribute("person") PersonDto personDto,
                                BindingResult bindingResult) {
       var message= personAgeValidatorService.validatePersonAge(personDto.getDateOfBirthday());
       if (message!=null){
           ObjectError error = new ObjectError(DATE_OF_BIRTHDAY_FOR_MODEL,message);
           bindingResult.addError(error);
       }
       if (bindingResult.hasErrors()) {
            return PERSON_PERSON_ADD_INFO_VIEW;
        } else {
            personService.createPersonFromPersonDtoAndSave(personDto);
            return "redirect:/addPatientToNewHistory";
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/updatePerson/{id}")
    public String infoUpdatePerson(@PathVariable Integer id,
                                   Model model) {
        try {
            var personDto = personService.createPersonDtoFromPerson(id);
            model.addAttribute(PERSON_FOR_MODEL, personDto);
            return PERSON_PERSON_ADD_INFO_VIEW;
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_DOCTOR')")
    @GetMapping("/patients")
    @ResponseStatus(HttpStatus.CREATED)
    public String selectOfPatients(Model model) {
            List<Person> persons = personService.findAll();
            model.addAttribute(PERSONS_FOR_MODEL, persons);
            return PERSON_PATIENTS_VIEW;
    }

    @GetMapping("/patient")
    public String patientInfo(@RequestParam("value") String id, Model model) {
        if (personService.checkRequestParameterFromSelect(id))
            return "redirect:/addNewHistory/" + id;

        var persons = personService.findAll();
        model.addAttribute(ERROR_FOR_MODEL, true);
        model.addAttribute(PERSON_FOR_MODEL, persons);
        return PERSON_PATIENTS_VIEW;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/deletePerson/{id}")
    public String deletePerson(@PathVariable Integer id,
                               Model model) {
        try {
            personService.deleteById(id);
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return REDIRECT_PERSONS_PAGE;
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/searchPerson")
    public String searchPerson(@RequestParam("keyword") String keyword, Model model) {
        if (keyword.isEmpty()) {
            return REDIRECT_PERSONS_VIEW;
        } else {
            List<Person> resultPersons = personService.search(keyword);
            model.addAttribute(RESULT_FOR_MODEL, resultPersons);
            model.addAttribute(KEYWORD_FOR_MODEL, keyword);
            return PERSON_SEARCH_PERSON_VIEW;

        }
    }

    @GetMapping("/searchPersonal")
    public String searchPersonal(@RequestParam("keyword") String keyword, Model model) {
        if (keyword.isEmpty()) {
            return "redirect:/searchPersonal/1";
        } else {
            List<Person> resultPersons = personService.searchAndFilterPersons(keyword);
            model.addAttribute(KEYWORD_FOR_MODEL, keyword);
            model.addAttribute(RESULT_FOR_MODEL, resultPersons);
            return PERSON_SEARCH_PERSONAL_VIEW;

        }
    }

    @GetMapping("/searchPersonal/{pageNumber}")
    public String getPersonalPage(@PathVariable("pageNumber") int pageNumber,Model model) {
        try {
            Page<Person> personPage = personService.getPageOfPersonWithRoleId(3, pageNumber);
            var personList = personPage.getContent();
            model.addAttribute(PAGE_FOR_MODEL, personPage);
            model.addAttribute(PERSONS_FOR_MODEL, personList);
            model.addAttribute(PAGE_NUMBER_FOR_MODEL, pageNumber);
            return "person/personal-list";
        } catch (PersonException e) {
            return "redirect:/searchPersonal/1";
        }
    }

    @GetMapping("/personal/{id}")
    public String getPersonal(@PathVariable("id") Integer id, Model model) {
        try {
            var personal = personService.checkAndFindPersonal(id);
            model.addAttribute(PERSON_FOR_MODEL, personal);
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return PERSON_PERSON_INFO_VIEW;
    }

}
