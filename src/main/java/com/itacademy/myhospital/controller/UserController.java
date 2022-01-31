package com.itacademy.myhospital.controller;


import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.validator.UserEmailValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final PersonService personService;
    private final RoleService roleService;
    public final AppointmentService appointmentService;
    public final UserEmailValidatorService userEmailValidatorService;

    @SneakyThrows
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/users/{pageNumber}")
    public String userListByPage(Model model,
                                 @PathVariable("pageNumber") int pageNumber,
                                 @RequestParam("sortField") String sortField,
                                 @RequestParam("sortDirection") String sortDirection) {



            var page = userService.findAll(pageNumber, sortField, sortDirection);
            var users = page.getContent();
            model.addAttribute(PAGE_FOR_MODEL, page);
            model.addAttribute(USERS_FOR_MODEL, users);
            model.addAttribute(SORT_FIELD_FOR_MODEL, sortField);
            String reverseSortDirection = sortDirection.equals(ASC_FOR_SORT_DIRECTION) ?
                    DESC_FOR_SORT_DIRECTION : ASC_FOR_SORT_DIRECTION;
            model.addAttribute(SORT_DIRECTION_FOR_MODEL, sortDirection);
            model.addAttribute(REVERSE_SORT_DIRECTION_FOR_MODEL, reverseSortDirection);

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
    public String userById(@PathVariable int id, Model model) throws UserException {
            var user = userService.getDtoById(id);
            model.addAttribute(USER_FOR_MODEL, user);
            return "user/user-info";

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
                                       Model model) throws UserException {
            var user = userService.getDtoByIdForSettings(id);
            var roles = roleService.findAll();
            model.addAttribute(ROLES_FOR_MODEL, roles);
            model.addAttribute(USER_FOR_MODEL, user);

        return "user/edit-user-for-admin";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updateUser")
    public String updateUser(@Valid @ModelAttribute("user") UserDto user,
                             BindingResult bindingResult,
                             @RequestParam("userImg") MultipartFile multipartFile) throws IOException, MessagingException, UserException, PersonException {

        if (bindingResult.hasErrors()) {
            return "user/edit-user";
        }
            userService.saveUpdatedUser(user, multipartFile);
        return "redirect:/userProfile";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/updateUserByAdmin")
    @Transactional
    public String updateUserByAdmin(@Valid @ModelAttribute("user") UserDto user,
                                    BindingResult bindingResult,
                                    @RequestParam("userImg") MultipartFile multipartFile) throws IOException, PersonException, MessagingException, UserException {
            if (bindingResult.hasErrors())
                return "user/edit-user-for-admin";
            if (userService.checkChangeOfRoles(user))
                appointmentService.addAppointmentsForNewDoctorForWeek(user.getUsername());
            userService.saveUpdatedUser(user, multipartFile);
            return "redirect:/user/" + user.getId();


    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id) throws UserException {
            userService.deleteById(id);
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/userAuthentication")
    public String userAuthentication() {
        return "authentication-key";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/authentication")
    public String makeAuthentication(@RequestParam("key") String key, Principal principal, Model model) throws UserException {
        try {
            var person = personService.addUserToPerson(key, principal.getName());
            return "redirect:/person/" + person.getId();
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL, "Person also has a user or no person with this key");
            return "authentication-key";
        }

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updatePasswordOfUser")
    public String updatePasswordOfUser(@Valid @ModelAttribute("user") UserDto user,
                                       BindingResult bindingResult) throws UserException {
        var message =userEmailValidatorService.validateEmail(user.getEmail());
        if (message!=null){
            ObjectError emailError = new ObjectError("email",message);
            bindingResult.addError(emailError);
        }
        if (bindingResult.hasErrors()) {
            return "user/edit-user";
        } else {
                userService.updatePasswordOfUser(user);
            return "redirect:/";
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/updatePasswordOfUserByAdmin")
    public String updatePasswordOfUserByAdmin(@Valid @ModelAttribute("user") UserDto user,
                                              BindingResult bindingResult) throws UserException {
        if (bindingResult.hasErrors()) {
            return "user/edit-user-for-admin";
        } else {
                userService.updatePasswordOfUser(user);
        }
        return "redirect:/user" + user.getId();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/searchUser")
    public String searchUser(@RequestParam("keyword") String keyword, Model model){

        if (keyword.isBlank()){
          return "redirect:/users/1?sortField=username&sortDirection=asc";
        }
        var users = userService.findByUsernameIsStartingWith(keyword);
        model.addAttribute(USERS_FOR_MODEL,users);
        model.addAttribute(KEYWORD_FOR_MODEL,keyword);
        return "user/search-user";
    }
}
