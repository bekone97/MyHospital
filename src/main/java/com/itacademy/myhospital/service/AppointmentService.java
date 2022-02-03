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

    Page<Appointment> findAll(int pageNumber, String sortField, String sortDirection);
    List<Appointment> findAll() ;
    Appointment findById(Integer id) throws AppointmentException;
    void saveAndFlush(Appointment appointment);
    void deleteById(Integer id) throws AppointmentException;
    boolean existsByDateOfAppointment(Timestamp dateOfAppointment);
    /**
     * This method returns all user appointments
     * @param user - searching of appointments by this user
     * @return list of appointments
     */
    List<Appointment> findByUserPatient(User user);
    /**
     * This method returns all future user appointments
     * @param user  - searching of appointments by this user
     * @param dateOfAppointment - after what date to search
     * @return list of appointments
     */
    List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user, Timestamp dateOfAppointment) ;
    /**
     * This method finds persons with the ROLE_DOCTOR, checks and, if necessary, creates appointments
     * @param currentDate - The date from which to start creating appointments
     * @param limitDate - The date from which to finish creating appointments
     * @throws AppointmentException if there are no persons with the role
     */
    void addAppointmentsForPersonalForWeek(LocalDateTime currentDate, LocalDateTime limitDate) throws AppointmentException;
    /**
     * This method creates appointments for ф doctor for a week
     * @param username - name of user for the doctor
     * @throws PersonException if a user doesn't have a person
     */
    void addAppointmentsForNewDoctorForWeek(String username) throws PersonException;
    /**
     * This method creates list of dates for a week without holidays
     * @param date - the date from which to start
     * @return list of dates
     */
    List<LocalDate> createListOfDays(LocalDate date);
    /**
     * This method returns list of a person's appointments on a certain date
     * @param date - date of appointments
     * @param person - whose appointments
     * @return list of appointments
     */
    List<Appointment> findAppointmentsOfDoctorOnDay(String date, Person person) throws AppointmentException;
    /**
     * This method finds an appointment with the id ,user with the username in database,
     * checks if appointment is not blocked and its time should be later than now. Then
     * set isEngaged true, adds user to appointment and adds phone number to appointment,
     * and sends message to user's email
     * @param phoneNumber - phone number of user
     * @param username - name of user
     * @param id - id of appointment
     * @return true
     * @throws AppointmentException if there is no appointment with the id in database or the appointment is already blocked
     * @throws MessagingException if there are some problem with sending a  message
     * @throws UnsupportedEncodingException if there are some problem with sending a  message
     */
    boolean changeAppointmentOnBlockedValue(String phoneNumber, String username, Integer id) throws AppointmentException, MessagingException, UnsupportedEncodingException;
    /**
     * This method finds an appointment by id and a user by username. Checks if a user is a userPatient of the appointment
     * and make canceling of appointment
     * @param username - name of user
     * @param appointmentId - id of appointment
     * @return true value if the method has completed
     * @throws AppointmentException if there is no appointment with the id in database
     * @throws MessagingException if there are some problems with sending a message
     * @throws UnsupportedEncodingException if there are some problems with sending a message
     * @throws UserException if there is no user with the username in database
     */
    boolean cancelAppointmentByUser(String username, Integer appointmentId) throws AppointmentException, MessagingException, UnsupportedEncodingException, UserException;
    /**
     *  This method finds an appointment by id and a user by username. Checks if a user has enough permissions
     * and make canceling of appointment
     * @param appointmentId - id of appointment
     * @param username - name of user
     * @return true value if the method has completed
     * @throws MessagingException if there are some problems with sending a message
     *  @throws UnsupportedEncodingException if there are some problems with sending a message
     * @throws AppointmentException if there is no appointment with the id in database
     * @throws UserException if there is no user with the username in database
     */
    boolean cancelAppointmentByDoctor(Integer appointmentId,String username) throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException;
    /**
     * This method checks for appointments by the date and if there is no appointments creates them
     * @param dateOfAppointments - date for meetings
     * @param personalList - list of persons with the ROLE_DOCTOR
     */
    void addAppointmentsForPersonal(LocalDateTime dateOfAppointments, List<Person> personalList);
    /**
     * This method find persons with the ROLE_DOCTOR , and creates appointments for them for the day
     * @param dateOfAppointments  date for creating appointments
     * @throws AppointmentException no appointments
     */
    void addAppointmentsForPersonalForDay(LocalDateTime dateOfAppointments) throws AppointmentException;
    List<Appointment> findAppointmentsByPersonalAndAndDateOfAppointmentBetween(Person person,
                                                                                      Timestamp dateOfAppointmentAfter,
                                                                                      Timestamp dateOfAppointmentBefore);
    /**
     * Thes method changes engaged status of the appointment on true value and save it
     * @param id - id of appointment
     * @param username - name of user
     * @return date of appointment
     * @throws AppointmentException if there is no appointment with this id in database or user doesn't have enough
     * permissions to block it
     */
    LocalDate blockAppointmentByDoctor(Integer id, String username) throws AppointmentException;
    /**
     * This method changes engaged status of the appointment on false value and save it
     * @param id - id of appointment
     * @param username - name of user
     * @return date of appointment
     * @throws AppointmentException if there is no appointment with this id in database or user doesn't have enough
     * permissions to block it
     */
    LocalDate unblockAppointmentByDoctor(Integer id, String username) throws AppointmentException;
    /**
     * This method returns a list of the person's appointments on the date
     * @param date - date of appointment
     * @param username - name of user
     * @return doctor's list of appointments on this date
     * @throws PersonException where is no person with the username of user in database
     */
    List<Appointment> getAppointmentsOfDoctorByDate(LocalDate date,String username) throws PersonException;

}
