package com.itacademy.myhospital.controller;


import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.AppointmentService;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UserService;
import com.itacademy.myhospital.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class UserController {

    public static final String ERROR = "error";
    private final UserService userService;
    private final PersonService personService;
    private final RoleService roleService;
    public final AppointmentService appointmentService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/users/{pageNumber}")
    public String userListByPage(Model model,
                                 @PathVariable("pageNumber") int pageNumber,
                                 @RequestParam("sortField") String sortField,
                                 @RequestParam("sortDirection") String sortDirection){

        Page<User> page = null;
        try {
            page = userService.findAll(pageNumber,sortField,sortDirection);
        var users = page.getContent();
        model.addAttribute("page",page);
        model.addAttribute("users",users);
        model.addAttribute("sortField",sortField);
        String reverseSortDirection = sortDirection.equals("asc") ? "desc" : "asc";
        model.addAttribute("sortDirection",sortDirection);
        model.addAttribute("reverseSortDirection",reverseSortDirection);
        } catch (UserException e) {

            return "redirect:/users/1?sortField=id&sortDirection=asc";
        }
        return "user/users";
    }
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping(value = "/userProfile")
    public String userInfo(Principal principal,Model model){
            try {
                var user = userService.getDtoByUsernameForProfile(principal.getName());
                model.addAttribute("user", user);
            } catch (UserException e) {
                return "redirect:/";
            }
        return "user/user-info";
    }
    @PostAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value ="/user/{id}")
    public String userById(@PathVariable int id, Model model)  {
        try {
            var user = userService.getDtoById(id);
            model.addAttribute("user",user);
            return "user/user-info";
        } catch (UserException e) {
            return "redirect:/users/1?sortField=id&sortDirection=asc";
        }
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/userSettings")
    public String userSettingsForUser(Principal principal,
                                   Model model){
            var user = userService.getDtoByUsernameForSettings(principal.getName());
            model.addAttribute("user",user);

        return "user/edit-user";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/userSettings/{id}")
    public String userSettingsForAdmin(@PathVariable("id") Integer id,
                                   Model model) {
        try {
           var user = userService.getDtoByIdForSettings(id);
            var roles= roleService.findAll();
            model.addAttribute("roles",roles);
            model.addAttribute("user",user);
        } catch (UserException e) {
            return "redirect:/users/1?sortField=id&sortDirection=asc";

        }
        return "user/edit-user-for-admin";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/updateUser")
    public String updateUser(@Valid @ModelAttribute("user") UserDto user,
            BindingResult bindingResult,
            @RequestParam("userImg") MultipartFile multipartFile) throws IOException {
        try {
            if (bindingResult.hasErrors()){
                return "user/edit-user";
            }
            userService.saveUpdatedUser(user,multipartFile);
        } catch (UserException | PersonException e) {
            return "redirect:/login";
        } catch (MessagingException e) {
            return "redirect:/userSettings";
        }

        return "redirect:/userProfile";
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/updateUserByAdmin")
    public String updateUserByAdmin(@Valid @ModelAttribute("user") UserDto user,
                             BindingResult bindingResult,
                             @RequestParam("userImg") MultipartFile multipartFile) throws IOException {
        try {
            if (bindingResult.hasErrors())
                return "user/edit-user-for-admin";
            if (userService.checkChangeOfRoles(user))
                appointmentService.addAppointmentsForNewDoctorForWeek(user.getUsername());
            userService.saveUpdatedUser(user,multipartFile);
            return "redirect:/user/"+user.getId();
        } catch (UserException | PersonException e) {
            return "/error/405";
        } catch (MessagingException e) {
            return "error/exception";
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Integer id, Model model){
        try {
            userService.deleteById(id);
        } catch (UserException e) {
            return "redirect:/";
        }
        return "redirect:/users/1?sortField=id&sortDirection=asc";
    }
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @GetMapping("/userAuthentication")
    public String userAuthentication(){
       return "authentication-key";
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    @PostMapping("/authentication")
    public String makeAuthentication(@RequestParam("key") String key, Principal principal, Model model){
        try {
            var person=personService.addUserToPerson(key, principal.getName());
            return "redirect:/person/" + person.getId();
        } catch (PersonException e) {
            model.addAttribute("error","Person also has a user or no person with this key");
            return "authentication-key";
            } catch (UserException e) {
           return "redirect:/userProfile";
        }

    }
        @PreAuthorize("isAuthenticated()")
        @PostMapping("/updatePasswordOfUser")
    public String updatePasswordOfUser(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "user/edit-user";
        }else {
            try {
                userService.updatePasswordOfUser(user);
            } catch (UserException e) {
                return "redirect:/";
            }
            return "redirect:/";
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/updatePasswordOfUserByAdmin")
    public String updatePasswordOfUserByAdmin(@Valid @ModelAttribute("user") UserDto user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "user/edit-user-for-admin";
        }else {
            try {
                userService.updatePasswordOfUser(user);
            } catch (UserException e) {
                return "redirect:/";
            }
        }
        return "redirect:/user"+user.getId();
    }
    }


//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/updatePasswordOfUser")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String changePasswordOfUser(Principal principal,Model model){
//        try {
//           var user = userService.findByUsername(principal.getName());
//        model.addAttribute("user",user);
//        } catch (UserException e) {
//            model.addAttribute(ERROR,e.getMessage());
//            return "redirect:/";
//        }
//        return "user/change-information-of-user";
//    }
//    @GetMapping("/userProfile/{username}")
//    public String userProfile(@PathVariable String username,Model model){
//       var user=userService.findByUsername(username);
//        model.addAttribute("user",user);
//        return "user/user-profile";
//    }
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @GetMapping("/updatePasswordOfUser/{id}")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String changePasswordOfUser(@PathVariable("id") Integer id,Model model){
//        User user = null;
//        try {
//            user = userService.findById(id);
//        model.addAttribute(user);
//        model.addAttribute("user",user);
//        } catch (UserException e) {
//            return "redirect:/users/1?sorfield=id&sortDirection=asc";
//        }
//        return "user/change-information-of-user";
//        }
//
//    @PostMapping("/saveNewUser")
//    public String saveNewUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult){
//        if(bindingResult.hasErrors()){
//            return "user/user-add-info";
//        }else {
////            var updateUser= userService.findByUsername(user.getUsername());
////            if(updateUser!=null){
////                updateUser.getRoles().clear();
////                updateUser.setRoles(user.getRoles());
//                userService.saveAndFlush(user);
////            }
//            return "redirect:/";
//        }
//    }
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PostMapping("/updateRolesOfUser")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String updateRolesOfUser(@Valid @ModelAttribute("user") User user){
//            userService.updateRolesOfUser(user);
//        return "redirect:/";
//        }

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @GetMapping("/updateRolesOfUser/{id}")
//    @ResponseStatus(HttpStatus.CREATED)
//    public String changeRolesOfUser(@PathVariable Integer id,
//                                Model model){
//        try {
//           var user = userService.findById(id);
////            user.getRoles().clear();
//        var roles= roleService.findAll();
//        model.addAttribute("roles",roles);
//        model.addAttribute("user",user);
//        } catch (UserException e) {
//
//            return "redirect:/users/1?sorfield=id&sortDirection=asc";
//        }
//        return "user/change-roles-of-user";
//    }