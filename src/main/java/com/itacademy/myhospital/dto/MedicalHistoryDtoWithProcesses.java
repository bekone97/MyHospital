package com.itacademy.myhospital.dto;

import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.model.entity.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicalHistoryDtoWithProcesses {

    private Integer id;

    private String complain;

    private Person patient;

    private Diagnosis diagnosis;

    @Valid
    private List<MedicalHistoryProcess> medicalHistoryProcesses;
}
