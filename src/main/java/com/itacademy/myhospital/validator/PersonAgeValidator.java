package com.itacademy.myhospital.validator;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PersonAgeValidator implements ConstraintValidator<PersonAgeConstraint, String> {

    @Override
    public void initialize(PersonAgeConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!s.isBlank()) {
            try {
                var date = LocalDate.parse(s);
                return !(date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now()));
            }catch (DateTimeParseException e){
                return false;
            }
        }else {
            return false;
        }
    }




}
