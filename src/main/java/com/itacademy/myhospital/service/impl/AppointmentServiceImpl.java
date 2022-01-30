package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.AppointmentRepository;
import com.itacademy.myhospital.service.JobService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.service.emailService.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PersonService personService;
    private final UserService userService;
    private final EmailService emailService;
    public static final int ROLE_ID_FOR_PERSONAL = 2;


    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PersonService personService, UserService userService, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.personService = personService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public Page<Appointment> findAll(int pageNumber, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment findById(Integer id) throws AppointmentException {
        Appointment appointment;
        var optional = appointmentRepository.findById(id);
        if (optional.isPresent()) {
            appointment = optional.get();
        } else {
            throw new AppointmentException("The appointment doesn't exist");
        }
        return appointment;
    }

    @Override
    @Transactional
    public void saveAndFlush(Appointment appointment) {
        appointmentRepository.saveAndFlush(appointment);
    }

    @Override
    public void deleteById(Integer id) throws AppointmentException {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
        } else {
            throw new AppointmentException("Not found an appointment");
        }
    }

    @Override
    public boolean existsByDateOfAppointment(Timestamp dateOfAppointment) {
        return appointmentRepository.existsByDateOfAppointment(dateOfAppointment);
    }

    /**
     * This method returns all user appointments
     * @param user - searching of appointments by this user
     * @return list of appointments
     */
    @Override
    public List<Appointment> findByUserPatient(User user) {

        return appointmentRepository.findByUserPatient(user);
    }

    /**
     * This method returns all future user appointments
     * @param user  - searching of appointments by this user
     * @param dateOfAppointment - after what date to search
     * @return list of appointments
     */
    @Override
    @Transactional
    public List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user,
                                                                        Timestamp dateOfAppointment) {
        return appointmentRepository.findByUserPatientAndDateOfAppointmentAfter(user,
                dateOfAppointment);
    }

    /**
     * This method creates appointments for the doctor for a week
     * @param username - name of user for the doctor
     * @throws PersonException if a user doesn't have a person
     */
    @Transactional
    public void addAppointmentsForNewDoctorForWeek(String username) throws PersonException {
        var person = personService.findPersonByUsernameOfUser(username);
        if (person!=null) {
            var currentDate = LocalDateTime.of(LocalDate.now(),LocalTime.of(8,0));
            var limitDate = currentDate.plusDays(8);
            List<Person> personList = new ArrayList<>();
            personList.add(person);
            checkAppointmentsExistForEveryDayAndCreateAppointments(currentDate, limitDate, personList);
        }else {
            throw new PersonException("User doesn't have a person");
        }
    }

    /**
     * This method finds persons with the ROLE_DOCTOR, checks and, if necessary, creates appointments
     * @param currentDate - The date from which to start creating appointments
     * @param limitDate - The date from which to finish creating appointments
     * @throws AppointmentException if there are no persons with the role
     */
    @Transactional
    public void addAppointmentsForPersonalForWeek(LocalDateTime currentDate, LocalDateTime limitDate) throws AppointmentException {
            var personalList = personService.findPersonsByRoleId(ROLE_ID_FOR_PERSONAL);
            checkAppointmentsExistForEveryDayAndCreateAppointments(currentDate, limitDate, personalList);
    }

    /**
     * This method find persons with the ROLE_DOCTOR , and creates appointments for them for the day
     * @param dateOfAppointments  date for creating appointments
     */
    @Transactional
    public void addAppointmentsForPersonalForDay(LocalDateTime dateOfAppointments) {
            var personalList = personService.findPersonsByRoleId(ROLE_ID_FOR_PERSONAL);
            addAppointmentsForPersonal(dateOfAppointments, personalList);
    }

    @Override
    public List<Appointment> findAppointmentsByPersonalAndAndDateOfAppointmentBetween(Person person,
                                                                                      Timestamp dateOfAppointmentAfter,
                                                                                      Timestamp dateOfAppointmentBefore){
        return appointmentRepository.findAppointmentsByPersonalAndAndDateOfAppointmentBetween(person,
                dateOfAppointmentAfter,
                dateOfAppointmentBefore);
    }

    private void checkAppointmentsExistForEveryDayAndCreateAppointments(LocalDateTime currentDate,
                                                                        LocalDateTime limitDate,
                                                                        List<Person> personalList) {
        if ((!existsByDateOfAppointment(Timestamp.valueOf(limitDate.minusDays(1))))) {
            while (!currentDate.equals(limitDate)) {
                addAppointmentsForPersonal(currentDate, personalList);
                currentDate = currentDate.plusDays(1);
            }
        }
    }

    /**
     * This method checks for appointments by the date and if there is no appointments creates them
     * @param dateOfAppointments - date for meetings
     * @param personalList - list of persons with the ROLE_DOCTOR
     */
    //Complete once a day. Check if appointments exist by this date , if not  - create appointments of this day
    @Transactional
    public void addAppointmentsForPersonal(LocalDateTime dateOfAppointments, List<Person> personalList) {
        if (!existsByDateOfAppointment(Timestamp.valueOf(dateOfAppointments))) {
            for (Person person :
                    personalList) {
                checkDatesAndSaveAppointmentsForPersonForDay(dateOfAppointments, person);
            }
        }
    }


    /**
     * This method creates and save appointments for the person from 8 a.m. to 18 p.m. of the date
     * @param dateOfAppointments - date of appointments
     * @param person - the person for whom appointments are created
     */
    void checkDatesAndSaveAppointmentsForPersonForDay(LocalDateTime dateOfAppointments, Person person) {
        if (!(dateOfAppointments.getDayOfWeek().getValue() == 6 || dateOfAppointments.getDayOfWeek().getValue() == 7)) {
            while (dateOfAppointments.getHour() < 18) {
                createAndSaveAppointment(dateOfAppointments, person);
                dateOfAppointments = dateOfAppointments.plusMinutes(30);
            }
        }
    }

    private void createAndSaveAppointment(LocalDateTime currentDate, Person person) {
        var appointment = Appointment.builder()
                .dateOfAppointment(Timestamp.valueOf(currentDate))
                .personal(person)
                .isEngaged(false)
                .build();
        saveAndFlush(appointment);
    }


    /**
     * This method creates list of dates for a week without holidays
     * @param date - the date from which to start
     * @return list of dates
     */
    public List<LocalDate> createListOfDays(LocalDate date) {
        List<LocalDate> listOfDates = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (date.getDayOfWeek().getValue() != 6 && date.getDayOfWeek().getValue() != 7) {
                listOfDates.add(date);
            }
            date = date.plusDays(1);
        }
        return listOfDates;
    }

    /**
     * This method returns list of a person's appointments on a certain date
     * @param date - date of appointments
     * @param person - whose appointments
     * @return list of appointments
     */
    public List<Appointment> getAppointmentsOfDayAndDoctor(String date, Person person) {
        var parseDate = LocalDate.parse(date);
        var firstDate = Timestamp.valueOf(parseDate.atTime(0, 0));
        var secondDate = Timestamp.valueOf(parseDate.plusDays(1).atTime(0, 0));
        return findAppointmentsByPersonalAndAndDateOfAppointmentBetween(person,firstDate, secondDate);
    }


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
     * @throws MessagingException,UnsupportedEncodingException if there are some problem with sending a  message
     */
    @Transactional
    public boolean changeAppointmentOnBlockedValue(String phoneNumber, String username, Integer id) throws AppointmentException, MessagingException, UnsupportedEncodingException {
        var user =userService.findByUsername(username);
        var appointment = findById(id);
        if (!appointment.isEngaged()&&appointment.getDateOfAppointment().after(Timestamp.valueOf(LocalDateTime.now()))) {
            appointment.setPhoneNumber(phoneNumber);
            appointment.setEngaged(true);
            appointment.setUserPatient(user);
            emailService.sendEmailAboutMakeAppointment(appointment, user);
            saveAndFlush(appointment);
            return true;
        }else {
            throw new AppointmentException("Appointment is already blocked");
        }
    }

    /**
     * This method finds an appointment by id and a user by username. Checks if a user is a userPatient of the appointment
     * Removes a user, a phone number and changes engaged status on false value. Save an appointment
     * @param username - name of user
     * @param appointmentId - id of appointment
     * @return true value if the method has completed
     * @throws AppointmentException if there is no appointment with the id in database
     * @throws MessagingException,UnsupportedEncodingException if there are some problems with sending a message
     * @throws UserException if there is no user with the username in database
     */
    @Override
    @Transactional
    public boolean cancelAppointmentByUser(String username, Integer appointmentId) throws AppointmentException, MessagingException, UnsupportedEncodingException, UserException {
        var appointment = findById(appointmentId);
        var user = userService.findByUsername(username);
        if (checkUserForCancelAppointmentByUser(user, appointment)) {
            cancelAppointment(appointment);
            emailService.sendEmailAboutCanceledAppointmentByUser(appointment);
            saveAndFlush(appointment);
            return true;
        } else {
            throw new AppointmentException("User doesn't has  permissions to cancel the appointment");
        }
    }

    /**
     *  This method finds an appointment by id and a user by username. Checks if a user has enough permissions.
     *    Removes a user, a phone number and changes engaged status on false value. Save an appointment
     * @param appointmentId - id of appointment
     * @param username - name of user
     * @return true value if the method has completed
     * @throws MessagingException,UnsupportedEncodingException if there are some problems with sending a message
     * @throws AppointmentException if there is no appointment with the id in database
     * @throws UserException if there is no user with the username in database
     */
    @Override
    @Transactional
    public boolean cancelAppointmentByDoctor(Integer appointmentId, String username) throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException {
        var appointment = findById(appointmentId);
        var user = userService.findByUsername(username);
        var doctor = getPersonAndChekIt(user);
        boolean hasDoctorRole = checkUserForCancelAppointmentByDoctor(user);
        if (hasDoctorRole) {
            cancelAppointment(appointment);
            emailService.sendEmailAboutCanceledAppointmentByDoctor(appointment, doctor);
            saveAndFlush(appointment);
            return true;
        } else {
            throw new AppointmentException("User doesn't have permissions to cancel the appointment");
        }
    }


    private Person getPersonAndChekIt(User user) throws UserException {
        var doctor = personService.findByUser(user);
        if (doctor==null) {
            throw new UserException("User doesn't have person object");
        }
        return doctor;
    }

    /**
     * This method checks if the user equals to a userPatient of the appointment
     * @param user - whose permissions are being checked
     * @param appointment - for what appointment
     * @return true value if the user equals to a userPatient and false if doesn't
     * @throws UserException if the user equals to null
     */
    private boolean checkUserForCancelAppointmentByUser(User user, Appointment appointment) throws UserException {
        if (user != null) {
            return user.equals(appointment.getUserPatient());
        } else {
            throw new UserException("User doesn't exist with username");
        }
    }

    /**
     * This method checks if the user equals to a userPatient of the appointment
     * @param user - whose permissions are being checked
     * @return true value if the user has ROLE_DOCTOR and false if doesn't have
     * @throws UserException if the user equals to null
     */
    private boolean checkUserForCancelAppointmentByDoctor(User user) throws UserException {
        if (user != null) {
            return user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_DOCTOR"));
        } else {
            throw new UserException("User doesn't exist with username");
        }
    }

    /**
     * This method sets null to phoneNumber, userPatient of appointment and sets false to engaged status of appointment
     * @param appointment - appointment that we are canceling
     */
    private void cancelAppointment(Appointment appointment) {
        appointment.setEngaged(false);
        appointment.setPhoneNumber(null);
        appointment.setUserPatient(null);
    }

    /**
     * Thes method changes engaged status of the appointment on true value and save it
     * @param id - id of appointment
     * @param username - name of user
     * @return true value if the method has completed
     * @throws AppointmentException if there is no appointment with this id in database or user doesn't have enough
     * permissions to block it
     */
    @Transactional
    public boolean blockAppointmentByDoctor(Integer id, String username) throws AppointmentException {
        var appointment =findById(id);
        var doctor=personService.findPersonByUsernameOfUser(username);
        if (appointment.getPersonal()==doctor){
            appointment.setEngaged(true);
            saveAndFlush(appointment);
            return true;
        }else {
            throw new AppointmentException("User doesn't have permissions to block the appointment");
        }
    }
    /**
     * Thes method changes engaged status of the appointment on false value and save it
     * @param id - id of appointment
     * @param username - name of user
     * @return true value if the method has completed
     * @throws AppointmentException if there is no appointment with this id in database or user doesn't have enough
     * permissions to block it
     */
    @Override
    @Transactional
    public boolean unblockAppointmentByDoctor(Integer id, String username) throws AppointmentException {
        var appointment =findById(id);
        var doctor=personService.findPersonByUsernameOfUser(username);
        if (appointment.getPersonal()==doctor){
            appointment.setEngaged(false);
            saveAndFlush(appointment);
            return true;
        }else {
            throw new AppointmentException("User doesn't have permissions to unblock the appointment");
        }
    }

    /**
     * This method returns a list of the person's appointments on the date
     * @param date - date of appointment
     * @param username - name of user
     * @return list of appointments of this date
     * @throws PersonException where is no person with the username of user in database
     */
    @Override
    @Transactional
    public List<Appointment> getAppointmentsOfDoctorByDate(LocalDate date,String username) throws PersonException {
        var dateAfter=LocalDateTime.of(date, LocalTime.of(0,0));
        var dateBefore=dateAfter.plusDays(1);
        if (username!=null) {
            var doctor = personService.findPersonByUsernameOfUser(username);
            return findAppointmentsByPersonalAndAndDateOfAppointmentBetween(doctor,
                    Timestamp.valueOf(dateAfter),
                    Timestamp.valueOf(dateBefore));
        }else {
            throw new PersonException("Person doesn't exist");
        }
    }

}
