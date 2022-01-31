package com.itacademy.myhospital.controller;


import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.validator.UserEmailValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.itacademy.myhospital.constants.Constants.*;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final PersonService personService;
    private final RoleService roleService;
    public final AppointmentService appointmentService;
    public final UserEmailValidatorService userEmailValidatorService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/users/{pageNumber}")
    public String userListByPage(Model model,
                                 @PathVariable("pageNumber") int pageNumber,
                                 @RequestParam("sortField") String sortField,
                                 @RequestParam("sortDirection") String sortDirection) {


        try {
            var page = userService.findAll(pageNumber, sortField, sortDirection);
            var users = page.getContent();
            model.addAttribute(PAGE_FOR_MODEL, page);
            model.addAttribute(USERS_FOR_MODEL, users);
            model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
            String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                    DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
            model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
            model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);
        } catch (UserException e) {
            return "redirect:/users/1?sortField=id&sortDirection=asc";
        }
        return "user/users";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/userProfile")
    public String userInfo(Principal principal, Model model) {
        var user = userService.getDtoByUsernameForProfile(principal.getName());
        model.addAttribute(USER_FOR_MODEL, user);
        return "user/user-info";
    }

    @PostAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/user/{id}")
    public String userById(@PathVariable int id, Model model) {
        try {
            var user = userService.getDtoById(id);
            model.addAttribute(USER_FOR_MODEL, user);
            return "user/user-info";
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL, e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/userSettings")
    public String userSettingsForUser(Principal principal,
                                      Model model) {
        var user = userService.getDtoByUsernameForSettings(principal.getName());
        model.addAttribute(USER_FOR_MODEL, user);
        return "user/edit-user";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/userSettings/{id}")
    public String userSettingsForAdmin(@PathVariable("id") Integer id,
                                       Model model) {
        try {
            var user = userService.getDtoByIdForSettings(id);
            var roles = roleService.findAll();
            model.addAttribute(ROLES_FOR_MODEL, roles);
            model.addAttribute(USER_FOR_MODEL, user);
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;

        }
        return "user/edit-user-for-admin";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updateUser")
    public String updateUser(@Valid @ModelAttribute("user") UserDto user,
                             BindingResult bindingResult,
                             @RequestParam("userImg") MultipartFile multipartFile,Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            return "user/edit-user";
        }
        try {
            userService.saveUpdatedUser(user, multipartFile);
        } catch (UserException | PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        } catch (MessagingException e) {
            return ERROR_EMAIL_EXCEPTION_PAGE;
        }

        return "redirect:/userProfile";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/updateUserByAdmin")
    @Transactional
    public String updateUserByAdmin(@Valid @ModelAttribute("user") UserDto user,
                                    BindingResult bindingResult,
                                    @RequestParam("userImg") MultipartFile multipartFile,Model model) throws IOException {
        try {
            if (bindingResult.hasErrors())
                return "user/edit-user-for-admin";
            if (userService.checkChangeOfRoles(user))
                appointmentService.addAppointmentsForNewDoctorForWeek(user.getUsername());
            userService.saveUpdatedUser(user, multipartFile);
            return "redirect:/user/" + user.getId();
        } catch (UserException | PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        } catch (MessagingException e) {
            return ERROR_EMAIL_EXCEPTION_PAGE;
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id, Model model) {
        try {
            userService.deleteById(id);
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/userAuthentication")
    public String userAuthentication() {
        return "authentication-key";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/authentication")
    public String makeAuthentication(@RequestParam("key") String key, Principal principal, Model model) {
        try {
            var person = personService.addUserToPerson(key, principal.getName());
            return "redirect:/person/" + person.getId();
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL, "Person also has a user or no person with this key");
            return "authentication-key";
        } catch (UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updatePasswordOfUser")
    public String updatePasswordOfUser(@Valid @ModelAttribute("user") UserDto user,
                                       BindingResult bindingResult,
                                       Model model) {
        var message =userEmailValidatorService.validateEmail(user.getEmail());
        if (message!=null){
            ObjectError emailError = new ObjectError("email",message);
            bindingResult.addError(emailError);
        }
        if (bindingResult.hasErrors()) {
            return "user/edit-user";
        } else {
            try {
                userService.updatePasswordOfUser(user);
            } catch (UserException e) {
                model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
                return ERROR_EXCEPTION_PAGE;
            }
            return "redirect:/";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/updatePasswordOfUserByAdmin")
    public String updatePasswordOfUserByAdmin(@Valid @ModelAttribute("user") UserDto user,
                                              BindingResult bindingResult,
                                              Model model) {
        if (bindingResult.hasErrors()) {
            return "user/edit-user-for-admin";
        } else {
            try {
                userService.updatePasswordOfUser(user);
            } catch (UserException e) {
                model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
                return ERROR_EXCEPTION_PAGE;
            }
        }
        return "redirect:/user" + user.getId();
    }
}
