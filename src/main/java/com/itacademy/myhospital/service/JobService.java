package com.itacademy.myhospital.service;

import java.time.LocalDateTime;

public interface JobService {
    public void addAppointmentsForPersonal();
    public void addAppointmentsForDay();
    public LocalDateTime getCurrentDate();
}
