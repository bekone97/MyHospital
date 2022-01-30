package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonDto {
    private Integer id;

    @Size(min = 2, message = "firstName must be min 2 symbols ")
    @Size(max=30,message = "firstName must be max 30 symbols")
    @NotBlank(message = "First name is required field")
    private String firstName;

    @Size(min = 2, message = "surname must be min 2 symbols ")
    @Size(max=30,message = "surname must be max 30 symbols")
    @NotBlank(message = "surname is required field")
    private String surname;

    @Size(min = 2, message = "patronymic must be min 2 symbols ")
    @Size(max=30,message = "patronymic must be max 30 symbols")
    @NotBlank(message = "patronymic is required field")
    private String patronymic;

    @NotBlank(message = "adress of birthday is required field")
    private String address;

    @NotBlank(message = "date of birthday is required field")
    private String dateOfBirthday;

    @Pattern(regexp = "([^a-zA-Z][0-9]{12,15})",message = "Number must be between 12 - 15 symbols")
    @NotBlank(message = "Phone number is required field")
    private String phoneNumber;

    private User user;

    private String keyForUser;
}
