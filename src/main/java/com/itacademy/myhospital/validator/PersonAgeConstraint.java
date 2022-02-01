package com.itacademy.myhospital.validator;

import javax.transaction.Transactional;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PersonAgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PersonAgeConstraint {
    String message() default "Invalid age of person";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
