package com.itacademy.myhospital.service;

import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException;
    void sendEmailAboutMakeAppointment(Appointment appointment, User user)
            throws MessagingException, UnsupportedEncodingException;
    void sendEmailAboutCanceledAppointmentByDoctor(Appointment appointment,
                                                          Person person)
            throws MessagingException, UnsupportedEncodingException;
    void sendEmailAboutCanceledAppointmentByUser(Appointment appointment)
            throws MessagingException, UnsupportedEncodingException;
    void sendEmailAboutChangeEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;
}
