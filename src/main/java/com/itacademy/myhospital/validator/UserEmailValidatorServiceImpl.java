package com.itacademy.myhospital.validator;

import com.itacademy.myhospital.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserEmailValidatorServiceImpl implements UserEmailValidatorService{
    private final UserService userService;

    public UserEmailValidatorServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String validateEmail(String email) {
        String message= null;
        if (userService.findByEmail(email)!=null)
            message="There is already a user with such an email";
        return message;
    }
}
