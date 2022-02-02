package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    @NotBlank(message = "username is required field")
    @Size(min=6, message = "username must be more than 6 symbols")
    private String username;
    @NotBlank(message = "password is required field")
    @Size(min=6,message = "password must be more than 6 symbols")
    private String password;
    private Set<Role> roles;
    @Email
    private String email;
    private String img;
    private Boolean verificationStatus;
    private Boolean authenticationStatus;
    private Person person;
    @Transient
    public String getImagePath(){
        if(img==null||id==null) return null;

        return "/users-images/"+id+"/"+img;
    }
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", authenticationStatus=" + authenticationStatus +
                ", email='" + email + '\'' +
                '}';
    }


}
