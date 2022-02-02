package com.itacademy.myhospital.validator;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.service.UserService;
import org.springframework.stereotype.Component;


@Component
public class UserEmailAndUsernameValidatorImpl implements UserEmailAndUsernameValidator {
    private final UserService userService;

    public UserEmailAndUsernameValidatorImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String validateEmail(UserDto userDto) {
        String message =null;
       var user= userService.findByEmail(userDto.getEmail());
    if((user!=null && !user.getUsername().equals(userDto.getUsername())))
         message="Email also exists";
    return message;
    }
    @Override
    public String validateUsername(UserDto userDto) {
        String message =null;
        var user= userService.findByUsername(userDto.getEmail());
        if(user!=null)
            message="User with this username also exists";
        return message;
    }
}
