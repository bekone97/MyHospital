package com.itacademy.myhospital.model.repository;

import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
    boolean existsByDateOfAppointment(Timestamp dateOfAppointment);
    List<Appointment> findByUserPatient(User user);
    List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user, Timestamp dateOfAppointment);
    List<Appointment> findAppointmentsByPersonalAndAndDateOfAppointmentBetween(Person person,
                                                                                      Timestamp dateOfAppointmentAfter,
                                                                                      Timestamp dateOfAppointmentBefore);
}
