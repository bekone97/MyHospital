package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.service.AppointmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class JobServiceImpl {



    private final AppointmentService appointmentService;


    public JobServiceImpl(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
//The method is invoked while the project constructs
    @Scheduled(initialDelayString = "${schedule.start}",fixedDelay=Long.MAX_VALUE)
    public void checkAndAddAppointmentsForPersonalForWeek() {
         var currentDate = getCurrentDate();
        var limitDate = currentDate.plusDays(8);
        try {
            appointmentService.addAppointmentsForPersonalForWeek(currentDate,limitDate);
        } catch (AppointmentException e) {

        }

    }
//The method is invoked in 24 hours after the project constructs, and it is invoked every 24 hours
    @Scheduled(initialDelayString = "${schedule.work}", fixedDelayString = "${schedule.work}")
    public void addAppointmentsForDay()  {
        var currentDate = getCurrentDate();
        try {
            appointmentService.addAppointmentsForPersonalForDay(currentDate.plusDays(7));
        } catch (AppointmentException e) {
        }
    }


    public LocalDateTime getCurrentDate() {
        return LocalDateTime.of(LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth()
                , 8,
                0);
    }

}
