package com.itacademy.myhospital.validator;

import com.itacademy.myhospital.dto.UserDto;

public interface UserEmailAndUsernameValidator {
    String validateEmail(UserDto userDto);
    public String validateUsername(UserDto userDto);
}
