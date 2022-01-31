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

import static com.itacademy.myhospital.constants.Constants.*;

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
                                     Principal principal) throws HistoryOfCompletingProcessException, MedicalHistoryProcessException {
            var person = personService.findPersonByUsernameOfUser(principal.getName());
            var medicalHistoryProcess = medicalHistoryProcessService.findById(id);
                historyOfCompletingProcessService.checkNumberOfExecutionsAndCreateNewExecution(medicalHistoryProcess, person, result);
                return "redirect:/history/" + medicalHistoryProcess.getMedicalHistory().getId();
    }

    @PreAuthorize("hasRole('ROLE_NURSE')")
    @GetMapping("/processExecutionHistory/{id}")
    public String processExecutionHistory(@PathVariable Integer id, Model model) throws MedicalHistoryProcessException {
            var medicalProcess = medicalHistoryProcessService.findById(id);
            var historiesOfCompletingProcess =
                    historyOfCompletingProcessService.findByMedicalHistoryProcess(medicalProcess);
            model.addAttribute(EXECUTION_HISTORY_FOR_MODEL, historiesOfCompletingProcess);
            model.addAttribute(PROCESS_FOR_MODEL, medicalProcess);
            return "historyOfCompletingProcess/executions-of-process";

    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/deleteProcess/{id}")
    public String deleteProcess(@PathVariable Integer id) throws MedicalHistoryProcessException {
            var process = medicalHistoryProcessService.findById(id);
            historyOfCompletingProcessService.removeHistoryOfCompletingProcessesByMedicalHistoryProcess(process);
            medicalHistoryProcessService.deleteById(id);
            return "redirect:/history/" + process.getMedicalHistory().getId();

    }
}
