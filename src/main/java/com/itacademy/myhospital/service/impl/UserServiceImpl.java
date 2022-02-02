package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.model.repository.UserRepository;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UUIDService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.service.EmailService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.itacademy.myhospital.constants.Constants.*;

@Service
public class UserServiceImpl implements UserService {



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RoleService roleService;
    private final UUIDService uuidService;
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           EmailService emailService,
                           RoleService roleService, UUIDService uuidService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.roleService = roleService;
        this.uuidService = uuidService;
    }

    public Page<User> findAll(int pageNumber, String sortField, String sortDirection) throws UserException {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);
        var pageOFUsers = userRepository.findAll(pageable);
        if (pageOFUsers.getContent().isEmpty()) {
            throw new UserException("Wrong page");
        }
        return pageOFUsers;
    }

    public User findById(Integer id) throws UserException {
        return userRepository.findById(id)
                .orElseThrow(()->new UserException(NO_USER_EXCEPTION +id));
    }

    public boolean saveAndFlush(User item) {
        userRepository.saveAndFlush(item);
        return true;
    }


    public boolean deleteById(Integer id) throws UserException {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            throw new UserException("User doesn't exist to delete him");
        }
    }

    @Override
    public User findByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findByVerificationCode(String verificationCode) {
        return userRepository.findByVerificationCode(verificationCode);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Transactional
    public boolean updatePasswordOfUser(UserDto user) throws UserException {
        var updatedUser = findById(user.getId());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        saveAndFlush(updatedUser);
        return true;
    }


    @Transactional
    public boolean createCodeAndSaveUser(UserDto userDto) throws  MessagingException, UnsupportedEncodingException {
            var user = User.builder()
                    .username(userDto.getUsername())
                    .verificationCode(createRandomCode())
                    .verificationStatus(false)
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .email(userDto.getEmail())
                    .authenticationStatus(false)
                    .build();
                    emailService.sendEmail(user,LOCALHOST);
            saveAndFlush(user);
            return true;
    }

    private String createRandomCode() {
        return uuidService.getRandomString();
    }



    @Transactional
    public boolean checkAndChangeVerificationStatus(String code) {
        var user = findByVerificationCode(code);
        if (user == null || user.getVerificationStatus()) {
            return false;
        } else {
            user.setVerificationStatus(true);
            Set<Role> roles = new HashSet<>();
            roles.add(roleService.findById(4));
            user.setRoles(roles);
            saveAndFlush(user);
            return true;
        }
    }


    @Transactional
    public boolean saveUpdatedUser(UserDto user, MultipartFile multipartFile) throws UserException, IOException, MessagingException {
        var updatedUser = findById(user.getId());
        checkChangeOfEmail(user,updatedUser);

        uploadFile(updatedUser, multipartFile);

        saveAndFlush(updatedUser);
        return  true;
    }
    public boolean checkChangeOfRoles(UserDto user){
        var returnValue = false;
        var updateUser= findByUsername(user.getUsername());
        if (user.getRoles()!=updateUser.getRoles()){
           var isDoctor=user.getRoles().stream().filter(role -> role.getId()==2)
                    .findFirst();
           if (isDoctor.isPresent()){
               returnValue= true;
           }
            updateUser.setRoles(user.getRoles());
            saveAndFlush(updateUser);
            return returnValue;
        }
        return returnValue;
    }
    public boolean checkChangeOfEmail(UserDto user, User updatedUser) throws MessagingException, UnsupportedEncodingException{
        if (!Objects.equals(updatedUser.getEmail(), user.getEmail())) {
                updatedUser.setVerificationStatus(false);
                updatedUser.setRoles(null);
                updatedUser.setEmail(user.getEmail());
                updatedUser.setVerificationCode(createRandomCode());
                emailService.sendEmailAboutChangeEmail(updatedUser, LOCALHOST);
                return true;
            }
            return false;
    }


    private boolean uploadFile(User updatedUser, MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

        if (!Objects.equals(updatedUser.getImg(), fileName)) {
            updatedUser.setImg(fileName);
            String uploadDirectory = "./users-images/" + updatedUser.getId();

            Path uploadPath = Paths.get(uploadDirectory);

            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                FileUtils.cleanDirectory(new File(uploadPath.toString()));
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Could not save uploaded file:" + fileName, ioe);
            }
            return true;
        }else {
            return false;
        }
    }

    public UserDto getDtoByUsernameForProfile(String username) {
        var user = findByUsername(username);
        return getDtoFromUserForUserProfile(user);
    }

    @Override
    public List<User> findByUsernameIsStartingWith(String keyword) {
        return userRepository.findByUsernameIsStartingWith(keyword);
    }

    public UserDto getDtoByUsernameForSettings(String username) {
        var user = findByUsername(username);
        return getDtoFromUserForSettings(user);
    }

    public UserDto getDtoByIdForSettings(Integer id) throws UserException {
        var user = findById(id);
        return getDtoFromUserForSettings(user);
    }

    public UserDto getDtoById(Integer id) throws UserException {
        var user = findById(id);
        return getDtoFromUserForUserProfile(user);
    }

    private UserDto getDtoFromUserForUserProfile(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .img(user.getImg())
                .roles(user.getRoles())
                .authenticationStatus(user.getAuthenticationStatus())
                .verificationStatus(user.getVerificationStatus())
                .person(user.getPerson())
                .build();
    }


    /**
     * This method creates a userDto without a verification status of user, a verification code of user and a verifications
     * status of user
     * @param user - A user from which a dto is created
     * @return a userDto
     */
    private UserDto getDtoFromUserForSettings(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authenticationStatus(user.getAuthenticationStatus())
                .verificationStatus(user.getVerificationStatus())
                .email(user.getEmail())
                .img(user.getImg())
                .roles(user.getRoles())
                .build();
    }

}
