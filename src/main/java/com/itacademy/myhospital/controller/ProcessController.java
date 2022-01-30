package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.exception.ProcessException;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProcessController {


    public static final String ERROR_FOR_MODEL = "error";
    public static final String ERROR_EXCEPTION_PAGE = "error/exception";
    private final PersonService personService;
    private final MedicalHistoryService medicalHistoryService;


    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @GetMapping("/selectProcesses")
    public String selectQuantityOfProcesses(@Valid @ModelAttribute("history") MedicalHistoryDtoWithNumberOfProcesses historyDto,
                                            BindingResult bindingResult,
                                            Principal principal,
                                            Model model){
            if (bindingResult.hasErrors()){
                return "history/history-add-info";
            }else {
                Person personal = personService.findPersonByUsernameOfUser(principal.getName());
                if (personal!=null) {
                try {
                var dto =medicalHistoryService
                        .getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(historyDto,personal);
                model.addAttribute("history", dto);
                return "process/select-processes";
                } catch (ProcessException e) {
                    model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
                    return ERROR_EXCEPTION_PAGE;
                }
            }else {
                return "redirect:/";
            }
            }

    }
}
