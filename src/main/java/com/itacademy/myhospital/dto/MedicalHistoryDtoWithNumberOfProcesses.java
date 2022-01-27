package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalHistoryDtoWithNumberOfProcesses {
    private Integer id;

    private String complain;

    @NotNull(message = "Patient is required field")
    private Person patient;

    @Valid
    private Diagnosis diagnosis;
    @Min(value = 0,message = "Number must be more than 0 or equals 0")
    @Max(value = 15,message = "Number must be less than 16")
    private int numberOfOperations;
    @Min(value = 0,message = "Number must be more than 0 or equals 0")
    @Max(value = 15,message = "Number must be less than 16")
    private int numberOfProcedures;
    @Min(value = 0,message = "Number must be more than 0 or equals 0")
    @Max(value = 15,message = "Number must be less than 16")
    private int numberOfMedications;

}
