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

    //    returns all user appointments
    @Override
    public List<Appointment> findByUserPatient(User user) {
        var userAppointments = appointmentRepository.findByUserPatient(user);

        return userAppointments;
    }

    //return just future user appointments
    @Override
    public List<Appointment> findByUserPatientAndDateOfAppointmentAfter(User user,
                                                                        Timestamp dateOfAppointment) {
        var futureUserAppointments = appointmentRepository.findByUserPatientAndDateOfAppointmentAfter(user,
                dateOfAppointment);
        return futureUserAppointments;
    }

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
    //    Complete during the project constructs
    public void addAppointmentsForPersonalForWeek(LocalDateTime currentDate, LocalDateTime limitDate) throws AppointmentException {
            var personalList = personService.findPersonsByRoleId(ROLE_ID_FOR_PERSONAL);
            checkAppointmentsExistForEveryDayAndCreateAppointments(currentDate, limitDate, personalList);
    }

    //    Complete once a day
    public void addAppointmentsForPersonalForDay(LocalDateTime dateOfAppointments) throws AppointmentException {
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

    //Complete once a day. Check if appointments exist by this date , if not  - create appointments of this day
    public void addAppointmentsForPersonal(LocalDateTime dateOfAppointments, List<Person> personalList) {
        if (!existsByDateOfAppointment(Timestamp.valueOf(dateOfAppointments))) {
            for (Person person :
                    personalList) {
                checkDatesAndSaveAppointmentsForPersonForDay(dateOfAppointments, person);
            }
        }
    }


    private void checkDatesAndSaveAppointmentsForPersonForDay(LocalDateTime dateOfAppointments, Person person) {
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

    //    returns appointments list of selected date


    //    returns List of first seven date from today, and misses holidays
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

    //returns all employees appointments on that day
    public List<Appointment> getAppointmentsOfDayAndDoctor(String date, Person person) {
        var parseDate = LocalDate.parse(date);
        var firstDate = Timestamp.valueOf(parseDate.atTime(0, 0));
        var secondDate = Timestamp.valueOf(parseDate.plusDays(1).atTime(0, 0));
        return findAppointmentsByPersonalAndAndDateOfAppointmentBetween(person,firstDate, secondDate);
    }

    //    Adds phone number, user and changes status in appointment, and send email.
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

    @Override
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

    @Override
    public boolean cancelAppointmentByDoctor(Integer appointmentId, String username) throws MessagingException, UnsupportedEncodingException, AppointmentException, UserException {
        var appointment = findById(appointmentId);
        var user = userService.findByUsername(username);
        var doctor = getPersonAndChekIt(user);
        boolean hasDoctorRole = checkUserForCancelAppointmentByDoctor(user, appointment);
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

    // return true if user who make it equals user in appointment
    private boolean checkUserForCancelAppointmentByUser(User user, Appointment appointment) throws UserException {
        if (user != null) {
            return user.equals(appointment.getUserPatient());
        } else {
            throw new UserException("User doesn't exist with username");
        }
    }

    private boolean checkUserForCancelAppointmentByDoctor(User user, Appointment appointment) throws UserException {
        if (user != null) {
            return user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_DOCTOR"));
        } else {
            throw new UserException("User doesn't exist with username");
        }
    }

    private void cancelAppointment(Appointment appointment) {
        appointment.setEngaged(false);
        appointment.setPhoneNumber(null);
        appointment.setUserPatient(null);
    }

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

    @Override
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
    @Override
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
