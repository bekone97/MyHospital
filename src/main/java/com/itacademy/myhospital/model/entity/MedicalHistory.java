package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "receipt_date")
    private Timestamp receiptDate;

    @Column(name = "discharge_date")
    private Timestamp dischargeDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
            (name="diagnosis_id")
    private Diagnosis diagnosis;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Person patient;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "medicalHistory")
    private List<MedicalHistoryProcess> medicalHistoryProcesses;

    @Column(name = "status")
    private boolean dischargeStatus;

    @Column(name = "complain")
    private String complain;


    public MedicalHistory(Integer id, Timestamp receiptDate, Timestamp dischargeDate, Diagnosis diagnosis, Person patient) {
        this.id = id;
        this.receiptDate = receiptDate;
        this.dischargeDate = dischargeDate;
        this.diagnosis = diagnosis;
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "MedicalHistory{" +
                "id=" + id +
                ", receiptDate=" + receiptDate +
                ", dischargeDate=" + dischargeDate +
                ", diagnosis=" + diagnosis +
                ", patient=" + patient +
                '}';
    }
}

