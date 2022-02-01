package com.itacademy.myhospital.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PersonPhoneNumberValidator implements ConstraintValidator<PersonPhoneNumberConstraint,String> {
    @Override
    public void initialize(PersonPhoneNumberConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
     return  ((phoneNumber.startsWith("80")&&phoneNumber.matches("[0-9]{10,13}")) ||
             (phoneNumber.startsWith("375")&&phoneNumber.matches("[0-9]{11,13}")));
    }
}
