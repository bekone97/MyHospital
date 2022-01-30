package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.AppointmentException;
import com.itacademy.myhospital.service.AppointmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class JobServiceImpl {



    private final AppointmentService appointmentService;


    public JobServiceImpl(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * The method is invoked when the project has started(Except tests)
     */
    @Scheduled(initialDelayString = "${schedule.start}",fixedDelay=Long.MAX_VALUE)
    public void checkAndAddAppointmentsForPersonalForWeek() throws AppointmentException {
         var currentDate = getCurrentDate();
        var limitDate = currentDate.plusDays(8);
            appointmentService.addAppointmentsForPersonalForWeek(currentDate,limitDate);

    }

    /**
     * The method is called 24 hours after the project has started working and then called every 24 hours
     */
    @Scheduled(initialDelayString = "${schedule.work}", fixedDelayString = "${schedule.work}")
    public void addAppointmentsForDay() throws AppointmentException {
        var currentDate = getCurrentDate();
            appointmentService.addAppointmentsForPersonalForDay(currentDate.plusDays(7));
    }


    public LocalDateTime getCurrentDate() {
        return LocalDateTime.of(LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth()
                , 8,
                0);
    }

}
