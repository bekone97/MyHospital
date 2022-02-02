package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalHistoryDtoWithNumberOfProcesses {
    private Integer id;

    private String complain;

    @NotNull
    private Person patient;

    @Valid
    private Diagnosis diagnosis;
    @Min(value = 0)
    @Max(value = 15)
    private int numberOfOperations;
    @Min(value = 0)
    @Max(value = 15)
    private int numberOfProcedures;
    @Min(value = 0)
    @Max(value = 15)
    private int numberOfMedications;

}
