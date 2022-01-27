package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.exception.HistoryOfCompletingProcessException;
import com.itacademy.myhospital.exception.MedicalHistoryProcessException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.service.HistoryOfCompletingProcessService;
import com.itacademy.myhospital.service.MedicalHistoryProcessService;
import com.itacademy.myhospital.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HistoryOfCompletingProcessController {

    private final HistoryOfCompletingProcessService historyOfCompletingProcessService;
    private final PersonService personService;
    private final MedicalHistoryProcessService medicalHistoryProcessService;

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @PostMapping("/executionProcess/{id}")
    public String executionOfProcess(@PathVariable Integer id,
                                     @RequestParam("result") String result,
                                     Principal principal) {
        try {
            var person = personService.findPersonByUsernameOfUser(principal.getName());
            var medicalHistoryProcess = medicalHistoryProcessService.findById(id);
            try {
                historyOfCompletingProcessService.checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess, person, result);
                return "redirect:/history/" + medicalHistoryProcess.getMedicalHistory().getId();
            } catch (HistoryOfCompletingProcessException e) {
                return "redirect:/processExecutionHistory/"+medicalHistoryProcess.getId();
            }
        } catch (MedicalHistoryProcessException e) {
            return "error/405";
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
            return "error/405";
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/deleteProcess/{id}")
    public String deleteProcess(@PathVariable Integer id) {
        try {
            var process = medicalHistoryProcessService.findById(id);
            historyOfCompletingProcessService.removeHistoryOfCompletingProcessesByMedicalHistoryProcess(process);
            medicalHistoryProcessService.deleteById(id);
            return "redirect:/history/" + process.getMedicalHistory().getId();
        } catch (MedicalHistoryProcessException e) {
            return "error/405";
        }
    }
}
