package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    @Size(min = 4,message = "Number must be more than 4 symbols")
   private String phoneNumber;
   private String dateOfAppointment;
   private Person personal;
}
