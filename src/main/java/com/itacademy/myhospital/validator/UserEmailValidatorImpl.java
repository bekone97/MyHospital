package com.itacademy.myhospital.validator;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.service.UserService;
import org.springframework.stereotype.Component;


@Component
public class UserEmailValidatorImpl implements UserEmailValidator{
    private final UserService userService;

    public UserEmailValidatorImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String validate(UserDto userDto) {
        String message =null;
       var user= userService.findByEmail(userDto.getEmail());
    if((user!=null && !user.getUsername().equals(userDto.getUsername())))
         message="Email also exists";
            return message;
    }
}
