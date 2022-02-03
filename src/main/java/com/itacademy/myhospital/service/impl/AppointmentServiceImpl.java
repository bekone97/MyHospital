package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.AppointmentRepository;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.itacademy.myhospital.constants.Constants.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PersonService personService;
    private final UserService userService;
    private final EmailService emailService;


    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PersonService personService, UserService userService, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.personService = personService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public Page<Appointment> findAll(int pageNumber, String sortField, String sortDirection) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment findById(Integer id) throws AppointmentException {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentException(NO_APPOINTMENT_EXCEPTION));
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
            throw new AppointmentException(NO_APPOINTMENT_EXCEPTION);
        }
    }

    @Override
    public boolean existsByDateOfAppointment(Timestamp dateOfAppointment) {
        return appointmentRepository.existsByDateOfAppointment(dateOfAppointment);
    }


    @Override
    public List<Appointment> findByUserPatient(User user) {

        return appointmentRepository.findByUserPatient(user);
    }


    @Override
    @Transactional
    public List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user,
                                                                        Timestamp dateOfAppointment) {
        return appointmentRepository.findByUserPatientAndDateOfAppointmentAfter(user,
                dateOfAppointment);
    }


    @Transactional
    public void addAppointmentsForNewDoctorForWeek(String username) throws PersonException {
        var person = personService.findPersonByUsernameOfUser(username);
        if (person != null) {
            var currentDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0));
            var limitDate = currentDate.plusDays(8);
            List<Person> personList = new ArrayList<>();
            personList.add(person);
            checkAppointmentsExistForEveryDayAndCreateAppointments(currentDate, limitDate, personList);
        } else {
            throw new PersonException(USER_WITHOUT_A_PERSON_EXCEPTION);
        }
    }


    @Transactional
    public void addAppointmentsForPersonalForWeek(LocalDateTime currentDate, LocalDateTime limitDate) throws AppointmentException {
        var personalList = personService.findPersonsByRoleId(ROLE_DOCTOR_ID);
        checkAppointmentsExistForEveryDayAndCreateAppointments(currentDate, limitDate, personalList);
    }


    @Transactional
    public void addAppointmentsForPersonalForDay(LocalDateTime dateOfAppointments) {
        var personalList = personService.findPersonsByRoleId(ROLE_DOCTOR_ID);
        addAppointmentsForPersonal(dateOfAppointments, personalList);
    }

    @Override
    public List<Appointment> findAppointmentsByPersonalAndAndDateOfAppointmentBetween(Person person,
                                                                                      Timestamp dateOfAppointmentAfter,
                                                                                      Timestamp dateOfAppointmentBefore) {
        return appointmentRepository.findAppointmentsByPersonalAndAndDateOfAppointmentBetween(person,
                dateOfAppointmentAfter,
                dateOfAppointmentBefore);
    }

    private void checkAppointmentsExistForEveryDayAndCreateAppointments(LocalDateTime currentDate,
                                                                        LocalDateTime limitDate,
                                                                        List<Person> personalList) {
        if ((!existsByDateOfAppointment(Timestamp.valueOf(limitDate.minusDays(1))) ||
                findAppointmentsByPersonalAndAndDateOfAppointmentBetween(personalList.get(0),
                        Timestamp.valueOf(currentDate),
                        Timestamp.valueOf(limitDate)).isEmpty())) {
            while (!currentDate.equals(limitDate)) {
                addAppointmentsForPersonal(currentDate, personalList);
                currentDate = currentDate.plusDays(1);
            }
        }
    }



    public void addAppointmentsForPersonal(LocalDateTime dateOfAppointments, List<Person> personalList) {
        if (!existsByDateOfAppointment(Timestamp.valueOf(dateOfAppointments))||findAppointmentsByPersonalAndAndDateOfAppointmentBetween(personalList.get(0),
                Timestamp.valueOf(dateOfAppointments),
                Timestamp.valueOf(dateOfAppointments.plusDays(1))).isEmpty()) {
            for (Person person :
                    personalList) {
                checkDatesAndSaveAppointmentsForPersonForDay(dateOfAppointments, person);
            }
        }
    }


    /**
     * This method creates and save appointments for the person from 8 a.m. to 18 p.m. of the date
     *
     * @param dateOfAppointments - date of appointments
     * @param person             - the person for whom appointments are created
     */
    private void checkDatesAndSaveAppointmentsForPersonForDay(LocalDateTime dateOfAppointments, Person person) {
        if (!(Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY).contains(dateOfAppointments.getDayOfWeek()))) {
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


    public List<LocalDate> createListOfDays(LocalDate date) {
        List<LocalDate> listOfDates = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (!Arrays.asList(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY).contains(date.getDayOfWeek())) {
                listOfDates.add(date);
            }
            date = date.plusDays(1);
        }
        return listOfDates;
    }


    public List<Appointment> findAppointmentsOfDoctorOnDay(String date, Person person) {
        var parseDate = LocalDate.parse(date);
        var firstDate = Timestamp.valueOf(parseDate.atTime(0, 0));
        var secondDate = Timestamp.valueOf(parseDate.plusDays(1).atTime(0, 0));
        return findAppointmentsByPersonalAndAndDateOfAppointmentBetween(person, firstDate, secondDate);
    }


    @Transactional
    public boolean changeAppointmentOnBlockedValue(String phoneNumber, String username, Integer id) throws AppointmentException, MessagingException, UnsupportedEncodingException {
        var user = userService.findByUsername(username);
        var appointment = findById(id);
        if (!appointment.isEngaged() && appointment.getDateOfAppointment().after(Timestamp.valueOf(LocalDateTime.now()))) {
            appointment.setPhoneNumber(phoneNumber);
            appointment.setEngaged(true);
            appointment.setUserPatient(user);
            emailService.sendEmailAboutMakeAppointment(appointment, user);
            saveAndFlush(appointment);
            return true;
        } else {
            throw new AppointmentException(APPOINTMENT_IS_ALREADY_BLOCKED_EXCEPTION);
        }
    }


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
            throw new AppointmentException(USER_HAS_NO_PERMISSIONS);
        }
    }


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
            throw new AppointmentException(USER_HAS_NO_PERMISSIONS);
        }
    }


    private Person getPersonAndChekIt(User user) throws UserException {
        var doctor = personService.findByUser(user);
        if (doctor == null) {
            throw new UserException(USER_WITHOUT_A_PERSON_EXCEPTION);
        }
        return doctor;
    }

    /**
     * This method checks if the user equals to a userPatient of the appointment
     *
     * @param user        - whose permissions are being checked
     * @param appointment - for what appointment
     * @return true value if the user equals to a userPatient and false if doesn't
     * @throws UserException if the user equals to null
     */
    private boolean checkUserForCancelAppointmentByUser(User user, Appointment appointment) throws UserException {
        if (user != null) {
            return user.equals(appointment.getUserPatient());
        } else {
            throw new UserException(NO_USER_WITH_USERNAME_EXCEPTION);
        }
    }

    /**
     * This method checks if the user equals to a userPatient of the appointment
     *
     * @param user - whose permissions are being checked
     * @return true value if the user has ROLE_DOCTOR and false if doesn't have
     * @throws UserException if the user equals to null
     */
    private boolean checkUserForCancelAppointmentByDoctor(User user) throws UserException {
        if (user != null) {
            return user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(ROLE_DOCTOR));
        } else {
            throw new UserException(NO_USER_WITH_USERNAME_EXCEPTION);
        }
    }

    /**
     * This method sets null to phoneNumber, userPatient of appointment and sets false to engaged status of appointment
     *
     * @param appointment - appointment that we are canceling
     */
    private void cancelAppointment(Appointment appointment) {
        appointment.setEngaged(false);
        appointment.setPhoneNumber(null);
        appointment.setUserPatient(null);
    }


    @Transactional
    public LocalDate blockAppointmentByDoctor(Integer id, String username) throws AppointmentException {
        var appointment = findById(id);
        var doctor = personService.findPersonByUsernameOfUser(username);
        if (appointment.getPersonal() == doctor) {
            appointment.setEngaged(true);
            saveAndFlush(appointment);
            return appointment.getDateOfAppointment().toLocalDateTime().toLocalDate();
        } else {
            throw new AppointmentException(USER_HAS_NO_PERMISSIONS);
        }
    }

    @Override
    @Transactional
    public LocalDate unblockAppointmentByDoctor(Integer id, String username) throws AppointmentException {
        var appointment = findById(id);
        var doctor = personService.findPersonByUsernameOfUser(username);
        if (appointment.getPersonal() == doctor) {
            appointment.setEngaged(false);
            saveAndFlush(appointment);
            return appointment.getDateOfAppointment().toLocalDateTime().toLocalDate();
        } else {
            throw new AppointmentException(USER_HAS_NO_PERMISSIONS);
        }
    }


    @Override
    @Transactional
    public List<Appointment> getAppointmentsOfDoctorByDate(LocalDate date, String username) throws PersonException {
        var dateAfter = LocalDateTime.of(date, LocalTime.of(0, 0));
        var dateBefore = dateAfter.plusDays(1);
        if (username != null) {
            var doctor = personService.findPersonByUsernameOfUser(username);
            return findAppointmentsByPersonalAndAndDateOfAppointmentBetween(doctor,
                    Timestamp.valueOf(dateAfter),
                    Timestamp.valueOf(dateBefore));
        } else {
            throw new PersonException(NO_PERSON_EXCEPTION);
        }
    }

}
