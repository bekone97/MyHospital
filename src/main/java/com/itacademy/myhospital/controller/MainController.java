package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    @GetMapping("/")
    public String mainPage(Principal principal,
                           Model model){
        if (principal!=null) {
                var user = userService.findByUsername(principal.getName());
                model.addAttribute("user", user);
        }
        return "home-page";
    }
}
