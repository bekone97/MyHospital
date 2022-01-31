package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import static com.itacademy.myhospital.constants.Constants.*;
@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    @GetMapping("/")
    public String mainPage(Principal principal,
                           Model model){
        if (principal!=null) {
                var user = userService.findByUsername(principal.getName());
                model.addAttribute(USER_FOR_MODEL, user);
        }
        return "home-page";
    }

    @GetMapping("/contacts")
    public String contacts(){
        return "contacts";
    }

}
