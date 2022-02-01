package com.itacademy.myhospital.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint,String> {
    @Override
    public void initialize(PhoneNumberConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
     return  ((phoneNumber.startsWith("80")&&phoneNumber.matches("[0-9]{10,13}")) ||
             (phoneNumber.startsWith("375")&&phoneNumber.matches("[0-9]{11,13}")));
    }
}
