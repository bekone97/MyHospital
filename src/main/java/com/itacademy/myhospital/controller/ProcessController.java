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

import static com.itacademy.myhospital.constants.Constants.*;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProcessController {



    private final PersonService personService;
    private final MedicalHistoryService medicalHistoryService;


    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @GetMapping("/selectProcesses")
    public String selectQuantityOfProcesses(@Valid @ModelAttribute("history") MedicalHistoryDtoWithNumberOfProcesses historyDto,
                                            BindingResult bindingResult,
                                            Principal principal,
                                            Model model) throws ProcessException {
            if (bindingResult.hasErrors()){
                return "history/history-add-info";
            }else {
                Person personal = personService.findPersonByUsernameOfUser(principal.getName());
                if (personal!=null) {
                var dto =medicalHistoryService
                        .getMedicalHistoryDtoWithProcessesFromDtoWithNumbers(historyDto,personal);
                model.addAttribute(HISTORY_FOR_MODEL, dto);
                return "process/select-processes";

            }else {
                return "redirect:/";
            }
            }

    }
}
