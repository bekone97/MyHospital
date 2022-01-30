package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.service.HistoryOfCompletingProcessService;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import com.itacademy.myhospital.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HistoryOfCompletingProcessController {

    public static final String ERROR_FOR_MODEL = "error";
    public static final String ERROR_EXCEPTION_PAGE = "error/exception";
    private final HistoryOfCompletingProcessService historyOfCompletingProcessService;
    private final PersonService personService;
    private final MedicalHistoryProcessService medicalHistoryProcessService;

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @PostMapping("/executionProcess/{id}")
    public String executionOfProcess(@PathVariable Integer id,
                                     @RequestParam("result") String result,
                                     Principal principal,
                                     Model model) {
        try {
            var person = personService.findPersonByUsernameOfUser(principal.getName());
            var medicalHistoryProcess = medicalHistoryProcessService.findById(id);
                historyOfCompletingProcessService.checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess, person, result);
                return "redirect:/history/" + medicalHistoryProcess.getMedicalHistory().getId();

        } catch (MedicalHistoryProcessException | HistoryOfCompletingProcessException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/processExecutionHistory/{id}")
    public String processExecutionHistory(@PathVariable Integer id, Model model) {
        try {
            var medicalProcess = medicalHistoryProcessService.findById(id);
            var historiesOfCompletingProcess =
                    historyOfCompletingProcessService.findByMedicalHistoryProcess(medicalProcess);
            model.addAttribute("executionHistory", historiesOfCompletingProcess);
            model.addAttribute("process", medicalProcess);
            return "historyOfCompletingProcess/executions-of-process";
        } catch (MedicalHistoryProcessException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/deleteProcess/{id}")
    public String deleteProcess(@PathVariable Integer id,
                                Model model) {
        try {
            var process = medicalHistoryProcessService.findById(id);
            historyOfCompletingProcessService.removeHistoryOfCompletingProcessesByMedicalHistoryProcess(process);
            medicalHistoryProcessService.deleteById(id);
            return "redirect:/history/" + process.getMedicalHistory().getId();
        } catch (MedicalHistoryProcessException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
}
