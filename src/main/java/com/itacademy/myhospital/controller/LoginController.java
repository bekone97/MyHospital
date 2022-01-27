package com.itacademy.myhospital.controller;


import com.itacademy.myhospital.dto.PersonDto;
import com.itacademy.myhospital.dto.UserDto;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.entity.Role;
import com.itacademy.myhospital.model.entity.User;
import com.itacademy.myhospital.service.PersonService;
import com.itacademy.myhospital.service.RoleService;
import com.itacademy.myhospital.service.UserService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;


@Controller
@RequestMapping()
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PreAuthorize("isAnonymous()")
   @GetMapping("/authorization")
    public String authorization(Model model){
       UserDto user = new UserDto();

       model.addAttribute("user",user);

       return "authorization";

   }
    @PreAuthorize("isAnonymous()")
    @PostMapping("/authorization")
    public String authorization(@Valid @ModelAttribute("user") UserDto user,
                                BindingResult bindingResult,
                                Model model){
       if (bindingResult.hasErrors()){
           return "authorization";
       }
        try {
            userService.createCodeAndSaveUser(user);
        } catch (UserException e) {
            model.addAttribute("error",e.getMessage());
            model.addAttribute("user",user);
            return "authorization";
        } catch (MessagingException |UnsupportedEncodingException e) {
            return "redirect:somethingWrong";
        }
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/verification")
    public String makeVerification(@RequestParam("code") String verificationCode){

       if (!userService.checkAndChangeVerificationStatus(verificationCode)){
           return "badVerivicationCode";
       }else {
           return "redirect:/";
       }
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpServletRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication==null || authentication instanceof AnonymousAuthenticationToken) {
            if (request.getParameterMap().get("error") != null)
                model.addAttribute("error", "You entered wrong parameters. Please try again");
            return "login";
        }else{
            return "redirect:/";
        }
    }

}
