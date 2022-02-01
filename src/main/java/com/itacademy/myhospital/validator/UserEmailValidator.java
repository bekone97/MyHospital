package com.itacademy.myhospital.validator;

import com.itacademy.myhospital.dto.UserDto;

public interface UserEmailValidator {
    String validate(UserDto userDto);
}
