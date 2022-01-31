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
    /**
     * This method checks username, creates a verification code for new user, encodes the password of new user, sends an
     * email message to a new user and saves a new user.
     * @param userDto This is dto of new user.
     * @return  If the method is successful, true is returned
     * @throws UserException If a user with the same name already exists, the method throws an error
     * @throws MessagingException,UnsupportedEncodingException If there are some problems with sending a message
     */
    boolean createCodeAndSaveUser(UserDto userDto) throws UserException, MessagingException, UnsupportedEncodingException;
    /**
     * This method changes a verification status on true value, adds the role_patient to a user,
     * makes verification code equals null and saves a user
     * @param code - the verification code of user
     * @return true if a user exists and its status is false or false if there is no user with the code or its status is true
     */
    boolean checkAndChangeVerificationStatus(String code);
    UserDto getDtoById(Integer id) throws UserException;
    UserDto getDtoByIdForSettings(Integer id) throws UserException;
    UserDto getDtoByUsernameForSettings(String username);
    /**
     *  This method is for encoding and changing password of user .
     * @param user this is Dto of user.
     * @return If the method has completed  return true
     * @throws UserException if there is no user with UserDto.getId in the database
     */
    boolean updatePasswordOfUser(UserDto user) throws UserException;
    /**
     * This method checks changing of an email , img , roles and makes these changes and saves a user.
     * @param user - a userDto with changes.
     * @param multipartFile - an img of user.
     * @return true
     * @throws UserException If there is no user with userDto.getId()
     * @throws IOException,MessagingException if there are some problems with sending message
     */
    boolean saveUpdatedUser(UserDto user,MultipartFile multipartFile) throws UserException, IOException, MessagingException, PersonException;
    boolean checkChangeOfRoles(UserDto user);
    UserDto getDtoByUsernameForProfile(String username);

}
