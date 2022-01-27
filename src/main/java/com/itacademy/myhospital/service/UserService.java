package com.itacademy.myhospital.service;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;


public interface UserService{
    public Page<User> findAll(int pageNumber,String sortField, String sortDirection) throws UserException;
    public User findById(Integer id) throws UserException;
    public boolean saveAndFlush(User item);
    public boolean deleteById(Integer id) throws UserException;

    public User findByUsername(String username);
    public User findByVerificationCode(String verificationCode);

    public User findByEmail(String email) throws UserException;
    public boolean createCodeAndSaveUser(UserDto user) throws UserException, MessagingException, UnsupportedEncodingException;
    public boolean checkAndChangeVerificationStatus(String code);
//    public void uploadFile(User user,
//                           MultipartFile multipartFile) throws IOException;
public UserDto getDtoById(Integer id) throws UserException;
    public UserDto getDtoByIdForSettings(Integer id) throws UserException;
    public UserDto getDtoByUsernameForSettings(String username);
    public boolean updatePasswordOfUser(UserDto user) throws UserException;
    public boolean saveUpdatedUser(UserDto user,MultipartFile multipartFile) throws UserException, IOException, MessagingException, PersonException;
    public boolean checkChangeOfRoles(UserDto user);
    public UserDto getDtoByUsernameForProfile(String username) throws UserException;

}
