package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "medical_history_processes")
public class MedicalHistoryProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @Column(name = "number_of_days")
    @Min(value = 1)
    @Max(value = 100)
    private int numberOfDays;


    @Column(name="quantity_per_day")
    @Min(value = 1)
    @Max(value = 100)
    private int quantityPerDay;

    @Valid
    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "name_of_process_id")
    private NameOfProcess nameOfProcess;

    @Column(name = "status")
    private boolean status;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "medical_history_id")
    private MedicalHistory medicalHistory;
}
