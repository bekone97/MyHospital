package com.itacademy.myhospital.service;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    public Page<Appointment> findAll(int pageNumber, String sortField, String sortDirection);
    public List<Appointment> findAll() ;
    public Appointment findById(Integer id) throws AppointmentException;
    public void saveAndFlush(Appointment appointment);
    public void deleteById(Integer id) throws AppointmentException;
    public boolean existsByDateOfAppointment(Timestamp dateOfAppointment);
    public List<Appointment> findByUserPatient(User user);
    public List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user, Timestamp dateOfAppointment) ;
    public void addAppointmentsForPersonalForWeek(LocalDateTime currentDate, LocalDateTime limitDate) throws AppointmentException;
    public void addAppointmentsForNewDoctorForWeek(String username) throws PersonException;
    public List<LocalDate> createListOfDays(LocalDate date);
    public List<Appointment> getAppointmentsOfDayAndDoctor(String date, Person person) throws AppointmentException;
    public boolean changeAppointmentOnBlockedValue(String phoneNumber, String username, Integer id) throws AppointmentException, MessagingException, UnsupportedEncodingException;
    public boolean cancelAppointmentByUser(String username, Integer appointmentId) throws AppointmentException, MessagingException, UnsupportedEncodingException, UserException;
    public boolean cancelAppointmentByDoctor(Integer appointmentId,String username) throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException;
    public void addAppointmentsForPersonal(LocalDateTime dateOfAppointments, List<Person> personalList);
    public void addAppointmentsForPersonalForDay(LocalDateTime dateOfAppointments) throws AppointmentException;
    public List<Appointment> findAppointmentsByPersonalAndAndDateOfAppointmentBetween(Person person,
                                                                                      Timestamp dateOfAppointmentAfter,
                                                                                      Timestamp dateOfAppointmentBefore);
    public boolean blockAppointmentByDoctor(Integer id, String username) throws AppointmentException;
    public boolean unblockAppointmentByDoctor(Integer id, String username) throws AppointmentException;
    public List<Appointment> getAppointmentsOfDoctorByDate(LocalDate date,String username) throws PersonException;

}
