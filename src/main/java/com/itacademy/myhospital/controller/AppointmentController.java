package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.AppointmentDto;
import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentController {
    public static final String ERROR_EMAIL_EXCEPTION_PAGE = "error/emailException";
    public static final String ERROR_EXCEPTION_PAGE = "error/exception";
    public static final String ERROR_FOR_MODEL = "error";
    public static final String APPOINTMENTS_FOR_MODEL = "appointments";
    public static final String CURRENT_DATE_TIME_FOR_MODEL = "currentLocalDateTime";
    public static final String DATES_FOR_MODEL = "dates";
    public static final String CURRENT_DATE_FOR_MODEL = "currentDate";
    public static final String USER_FOR_MODEL = "user";
    private final AppointmentService appointmentService;
    private final PersonService personService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/choiceOfDateAndDoctor")
    public String makeChoiceOfDateAndDoctor(Model model, Principal principal) {
        AppointmentDto appointment = new AppointmentDto();
        var listOfDates = appointmentService.createListOfDays(LocalDate.now());
        var listOfDoctors = personService.findPersonsByRoleId(2);
        var patient = personService.findPersonByUsernameOfUser(principal.getName());
        if (patient != null) {
            appointment.setPhoneNumber(patient.getPhoneNumber());
            model.addAttribute("patient", patient);
        }
        model.addAttribute("listOfDoctors", listOfDoctors);
        model.addAttribute("listOfDates", listOfDates);
        model.addAttribute("appointmentDto", appointment);

        return "appointment/choiceOfDateAndPersonal";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/choiceOfTime")
    public String makeChoiceTimeOfDay(@Valid AppointmentDto appointmentDto,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            var listOfDays = appointmentService.createListOfDays(LocalDate.now());
            var listOfDoctors = personService.findPersonsByRoleId(2);
            model.addAttribute("listOfDoctors", listOfDoctors);
            model.addAttribute("listOfDates", listOfDays);
            return "appointment/choiceOfDateAndPersonal";
        }
        try {
            var appointmentsOfDay =
                    appointmentService.getAppointmentsOfDayAndDoctor(appointmentDto.getDateOfAppointment(),
                            appointmentDto.getPersonal());
            model.addAttribute(APPOINTMENTS_FOR_MODEL, appointmentsOfDay);
            model.addAttribute("date", appointmentDto.getDateOfAppointment());
            model.addAttribute("phoneNumber", appointmentDto.getPhoneNumber());
            model.addAttribute(CURRENT_DATE_TIME_FOR_MODEL,LocalDateTime.now());
            return "appointment/choice-time-of-appointment";
        } catch (AppointmentException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/makeAppointment/{id}")
    public String makeAppointment(@PathVariable("id") Integer id,
                                  Principal principal,
                                  @RequestParam("phoneNumber") String phoneNumber,
                                  Model model) {
        try {
            appointmentService.changeAppointmentOnBlockedValue(phoneNumber, principal.getName(), id);
        } catch (AppointmentException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        } catch (MessagingException |UnsupportedEncodingException e) {
            return ERROR_EMAIL_EXCEPTION_PAGE;
         }
        return "redirect:/myCurrentAppointments";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/myCurrentAppointments")
    public String myAppointments(Principal principal, Model model){
        var user = userService.findByUsername(principal.getName());
        var userAppointments = appointmentService.findByUserPatientAndDateOfAppointmentAfter(user,
                Timestamp.valueOf(LocalDateTime.now()));
        model.addAttribute(USER_FOR_MODEL, user);
        model.addAttribute(APPOINTMENTS_FOR_MODEL, userAppointments);
        model.addAttribute(CURRENT_DATE_TIME_FOR_MODEL,LocalDateTime.now());
        return "appointment/user-appointments";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/myAllAppointments")
    public String myAllAppointments(Principal principal, Model model){
        var user = userService.findByUsername(principal.getName());
        var userAppointments = appointmentService.findByUserPatient(user);
        model.addAttribute(USER_FOR_MODEL, user);
        model.addAttribute(APPOINTMENTS_FOR_MODEL, userAppointments);
        model.addAttribute(CURRENT_DATE_TIME_FOR_MODEL,LocalDateTime.now());
        return "appointment/user-appointments";
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/currentAppointmentsOfUser/{id}")
    public String currentAppointmentsOfUser(@PathVariable("id") Integer id, Model model) {

        try {
        var user = userService.findById(id);
        var userAppointments = appointmentService.findByUserPatientAndDateOfAppointmentAfter(user,
                Timestamp.valueOf(LocalDateTime.now()));
        model.addAttribute(USER_FOR_MODEL, user);
        model.addAttribute(APPOINTMENTS_FOR_MODEL, userAppointments);
        return "appointment/user-appointments-for-doctor";
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/allAppointmentsOfUser/{id}")
    public String allAppointmentsOfUser(@PathVariable("id") Integer id, Model model){
        try {
        var user = userService.findById(id);
        var userAppointments = appointmentService.findByUserPatient(user);
        model.addAttribute(USER_FOR_MODEL, user);
        model.addAttribute(APPOINTMENTS_FOR_MODEL, userAppointments);
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return "appointment/user-appointments-for-doctor";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cancelAppointmentByUser/{id}")
    public String cancelAppointmentByUser(@PathVariable("id") Integer appointmentId,
                                          Principal principal,
                                          Model model) {
        try {
            appointmentService.cancelAppointmentByUser(principal.getName(), appointmentId);
        } catch (AppointmentException | UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        } catch (MessagingException |UnsupportedEncodingException e) {
            return ERROR_EMAIL_EXCEPTION_PAGE;
        }
        return "redirect:/myCurrentAppointments";
    }

    @PreAuthorize("hasAnyRole('ROLE_DOCTOR','ROLE_ADMIN')")
    @PostMapping("/cancelAppointmentByDoctor/{id}")
    public String cancelAppointmentByDoctor(@PathVariable("id") Integer appointmentId, Principal principal,
                                            Model model) {
        try {
            appointmentService.cancelAppointmentByDoctor(appointmentId, principal.getName());
        } catch (AppointmentException | UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        } catch (MessagingException |UnsupportedEncodingException e) {
            return ERROR_EMAIL_EXCEPTION_PAGE;
        }
        return "redirect:/myCurrentAppointments";
    }



    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/mySchedule")
    public String getMyAppointmentsSchedule(Principal principal,Model model){
        try {
            var appointments=appointmentService
                    .getAppointmentsOfDoctorByDate(LocalDate.now(),principal.getName());
         List<LocalDate> dates = appointmentService.createListOfDays(LocalDate.now());
            model.addAttribute(APPOINTMENTS_FOR_MODEL,appointments);
            model.addAttribute(DATES_FOR_MODEL,dates);
            model.addAttribute(CURRENT_DATE_FOR_MODEL,LocalDate.now());
        model.addAttribute(CURRENT_DATE_TIME_FOR_MODEL,LocalDateTime.now());
            return "appointment/appointments-schedule";
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/myScheduleByDate")
    public String getMyAppointmentsScheduleByDate(@RequestParam("date") String date, Principal principal,Model model){
        try {
        var appointments = appointmentService
                    .getAppointmentsOfDoctorByDate(LocalDate.parse(date),principal.getName());
        List<LocalDate> dates = appointmentService.createListOfDays(LocalDate.now());
        model.addAttribute(APPOINTMENTS_FOR_MODEL,appointments);
        model.addAttribute(DATES_FOR_MODEL,dates);
        model.addAttribute(CURRENT_DATE_FOR_MODEL,LocalDate.parse(date));
        model.addAttribute(CURRENT_DATE_TIME_FOR_MODEL,LocalDateTime.now());
        return "appointment/appointments-schedule";
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/blockAppointmentByDoctor/{id}")
    public String blockAppointmentByDoctor(@PathVariable("id") Integer id,
                                           Principal principal,
                                           Model model){
        try {
           appointmentService.blockAppointmentByDoctor(id,principal.getName());
        } catch (AppointmentException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return "redirect:/mySchedule";
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/unblockAppointmentByDoctor/{id}")
    public String unblockAppointmentByDoctor(@PathVariable("id") Integer id,
                                             Principal principal,
                                             Model model){
        try {
            appointmentService.unblockAppointmentByDoctor(id,principal.getName());
        } catch (AppointmentException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return "redirect:/mySchedule";
    }

}