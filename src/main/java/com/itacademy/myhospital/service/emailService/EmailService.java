package com.itacademy.myhospital.service.emailService;

import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    public void sendEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException;
    public void sendEmailAboutMakeAppointment(Appointment appointment, User user)
            throws MessagingException, UnsupportedEncodingException;
    public void sendEmailAboutCanceledAppointmentByDoctor(Appointment appointment,
                                                          Person person)
            throws MessagingException, UnsupportedEncodingException;
    public void sendEmailAboutCanceledAppointmentByUser(Appointment appointment)
            throws MessagingException, UnsupportedEncodingException;
    public void sendEmailAboutChangeEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException;
}
