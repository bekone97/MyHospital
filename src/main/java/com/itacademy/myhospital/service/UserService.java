package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public interface UserService{
    Page<User> findAll(int pageNumber,String sortField, String sortDirection) throws UserException;
    User findById(Integer id) throws UserException;
    boolean saveAndFlush(User item);
    boolean deleteById(Integer id) throws UserException;
    User findByUsername(String username);
    User findByVerificationCode(String verificationCode);
    User findByEmail(String email);
    boolean createCodeAndSaveUser(UserDto user) throws UserException, MessagingException, UnsupportedEncodingException;
    boolean checkAndChangeVerificationStatus(String code);
    UserDto getDtoById(Integer id) throws UserException;
    UserDto getDtoByIdForSettings(Integer id) throws UserException;
    UserDto getDtoByUsernameForSettings(String username);

    boolean updatePasswordOfUser(UserDto user) throws UserException;
    boolean saveUpdatedUser(UserDto user,MultipartFile multipartFile) throws UserException, IOException, MessagingException, PersonException;
    boolean checkChangeOfRoles(UserDto user);
    UserDto getDtoByUsernameForProfile(String username);

}
