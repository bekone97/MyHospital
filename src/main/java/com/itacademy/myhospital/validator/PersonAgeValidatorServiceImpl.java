package com.itacademy.myhospital.validator;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PersonAgeValidatorServiceImpl implements PersonAgeValidatorService {
public String validatePersonAge(String dateOfBirthday){
    String message= null;
    var date = LocalDate.parse(dateOfBirthday);
    if (date.isAfter(LocalDate.now()))
        message="Date must be less than now";
    return message;
}
}
