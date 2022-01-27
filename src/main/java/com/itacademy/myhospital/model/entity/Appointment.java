package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="appointments")
public class Appointment {
    public Appointment(Timestamp dateOfAppointment, boolean isEngaged, Person personal) {
        this.dateOfAppointment = dateOfAppointment;
        this.isEngaged = isEngaged;
        this.personal = personal;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_of_appointment")
    private Timestamp dateOfAppointment;

    @Column(name = "is_engaged")
    private boolean isEngaged;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne()
    @JoinColumn(name = "personal_id")
    private Person personal;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_patient_id")
    private User userPatient;


}
