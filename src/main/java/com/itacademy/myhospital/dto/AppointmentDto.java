package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.validator.PhoneNumberConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
   @PhoneNumberConstraint
   private String phoneNumber;
   private String dateOfAppointment;
   private Person personal;
}
