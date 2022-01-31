package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.model.entity.Appointment;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService {
    public static final String HOSPITAL_MAIL = "hospitalmyachin@gmail.com";
    public static final String SENDER_NAME = "My hospital";
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Override
    public void sendEmail(User user,String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress= user.getEmail();
        String subject="Please verify your registration";
        String content="Dear [[username]], <br>"
                +"Please click the link below to verify your registration: <br>"
                +"<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
//                +siteURL
                +"Thank you<br>"+
                "HospitalMyachin.";
        content=content.replace("[[username]]", user.getUsername());
        String verifyURL = siteURL + "/verification?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        createMessageAndSendIt(toAddress,subject,content);
    }
    public void sendEmailAboutChangeEmail(User user,
                                          String siteURL) throws MessagingException, UnsupportedEncodingException {
        String toAddress= user.getEmail();
        String subject="Please verify your email";
        String content="Dear [[username]], <br>"
                +"You have changed your email.Please click the link below to verify your email: <br>"
                +"<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
//                +siteURL
                +"Thank you<br>"+
                "HospitalMyachin.";
        content=content.replace("[[username]]", user.getUsername());
        String verifyURL = siteURL + "/verification?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        createMessageAndSendIt(toAddress,subject,content);
    }
    private void createMessageAndSendIt(String toAddress,String subject, String content) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(HOSPITAL_MAIL, SENDER_NAME);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);
        javaMailSender.send(message);
    }

    public void sendEmailAboutMakeAppointment(Appointment appointment, User user) throws MessagingException, UnsupportedEncodingException {

        String toAddress= user.getEmail();
        String subject="You have made an appointment";
        String content="Dear [[username]], <br>"
                +"You have made an appointment at [[date]] to [[doctor]]: <br>"
                +"Thank you<br>"+
                "HospitalMyachin.";

        content=content.replace("[[username]]", user.getUsername());
        content=content.replace("[[date]]",appointment.getDateOfAppointment().toString());
        content=content.replace("[[doctor]]",appointment.getPersonal().getFIO());
        createMessageAndSendIt(toAddress,subject,content);
    }


    public void sendEmailAboutCanceledAppointmentByUser(Appointment appointment) throws MessagingException, UnsupportedEncodingException {
        String toAddress= appointment.getUserPatient().getEmail();
        String subject="You have canceled the appointment";
        String content="Dear [[username]], <br>"
                +"The appointment at [[date]] to [[doctor]] was canceled  : <br>"
                +"Thank you<br>"+
                "HospitalMyachin.";
        content=content.replace("[[username]]", appointment.getUserPatient().getUsername());
        content=content.replace("[[date]]",appointment.getDateOfAppointment().toString());
        content=content.replace("[[doctor]]",appointment.getPersonal().getFIO());
        createMessageAndSendIt(toAddress,subject,content);
    }
    public void sendEmailAboutCanceledAppointmentByDoctor(Appointment appointment, Person person) throws MessagingException, UnsupportedEncodingException {
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String toAddress= appointment.getUserPatient().getEmail();
        String subject="Your appointment was canceled";
        String content="Dear [[username]], <br>"
                +"The appointment at [[date]] to [[doctor]] was canceled by [[FIO]] : <br>"
                +"Thank you<br>"+
                "HospitalMyachin.";
        content=content.replace("[[username]]", appointment.getUserPatient().getUsername());
        content=content.replace("[[date]]",appointment.getDateOfAppointment().toString());
        content=content.replace("[[doctor]]",appointment.getPersonal().getFIO());
        content=content.replace("[[FIO]]",person.getFIO());

        createMessageAndSendIt(toAddress,subject,content);
    }
}

