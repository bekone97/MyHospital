package com.itacademy.myhospital.controller;

import com.itacademy.myhospital.dto.MedicalHistoryDtoWithNumberOfProcesses;
import com.itacademy.myhospital.dto.MedicalHistoryDtoWithProcesses;
import com.itacademy.myhospital.exception.MedicalHistoryException;
import com.itacademy.myhospital.exception.PersonException;
import com.itacademy.myhospital.exception.UserException;
import com.itacademy.myhospital.model.entity.MedicalHistoryProcess;
import com.itacademy.myhospital.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

import static com.itacademy.myhospital.constants.Constants.*;


@Controller
@RequiredArgsConstructor
public class MedicalHistoryController {



    private final MedicalHistoryService medicalHistoryService;
    private final PersonService personService;
    private final ProcessService processService;
    private final MedicalHistoryProcessService medicalHistoryProcessService;
    private final DiagnosisService diagnosisService;
    private final NameOfProcessService nameOfProcessService;


    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @GetMapping("/addPatientToNewHistory")
    public String addPatientToNewHistory(Model model) {
        var persons = personService.findAll();
        model.addAttribute(PERSONS_FOR_MODEL, persons);

        return "person/patients";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/history/{id}")
    public String historyById(@PathVariable Integer id, Model model, Principal principal) {
        try {
            var history = medicalHistoryService.checkPersonForViewHistory(id,principal.getName());
            model.addAttribute(HISTORY_FOR_MODEL, history);
            return "history/history-info";
        } catch (MedicalHistoryException | UserException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping(value = "/checkHistory")
    public String checkNewHistory(@Valid @ModelAttribute("history") MedicalHistoryDtoWithProcesses dto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "process/select-processes";
        }
        dto = nameOfProcessService.checkNameOfProcesses(dto);
        medicalHistoryService.createAndSaveNewMedicalHistoryFromDto(dto);
        return "redirect:/person/" + dto.getPatient().getId();
    }

    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    @GetMapping("/addNewHistory/{id}")
    public String addNewHistory(@PathVariable Integer id, Model model, Principal principal) {
        MedicalHistoryDtoWithNumberOfProcesses medicalHistoryDto = new MedicalHistoryDtoWithNumberOfProcesses();
        try {
            var diagnoses =diagnosisService.getDiagnosesOfPerson(principal.getName());
            medicalHistoryDto.setPatient(personService.findById(id));
            model.addAttribute(HISTORY_FOR_MODEL, medicalHistoryDto);
            model.addAttribute(DIAGNOSES_FOR_MODEL, diagnoses);
            return "history/history-add-info";
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/addProcessToMedicalHistory/{id}")
    public String addProcessToMedicalHistory(@PathVariable("id") Integer id,
                                             Model model) {
        try {
           var history = medicalHistoryService.findById(id);
            if (!history.isDischargeStatus()) {
                var medicalHistoryProcess = new MedicalHistoryProcess();
                medicalHistoryProcess.setMedicalHistory(history);
                var processes = processService.findAll();
                model.addAttribute(MEDICAL_HISTORY_PROCESS_FOR_MODEL, medicalHistoryProcess);
                model.addAttribute(PROCESS_FOR_MODEL, processes);
                return "history/add-process-to-medical-history";
            } else {
                return "redirect:/history/" + history.getId();
            }
        } catch (MedicalHistoryException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping("/addProcessToMedicalHistory")
    public String saveProcessToMedicalHistory(@Valid @ModelAttribute("medicalHistoryProcess")
                                                          MedicalHistoryProcess medicalHistoryProcess,
                                              BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()){
            var processes = processService.findAll();
            model.addAttribute(PROCESSES_FOR_MODEL, processes);
            return "history/add-process-to-medical-history";
        }
            medicalHistoryProcess = medicalHistoryProcessService.checkAndSaveMedicalHistoryProcess(medicalHistoryProcess);
            return "redirect:/history/" + medicalHistoryProcess.getMedicalHistory().getId();
    }



    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @GetMapping("/dischargePatient/{id}")
    public String dischargePatient(@PathVariable Integer id,
                                   Model model) {
        try {
            medicalHistoryService.dischargePatient(id);
            return "redirect:/history/" + id;
        } catch (MedicalHistoryException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }

    @PreAuthorize("hasAuthority('ROLE_NURSE')")
    @GetMapping("/openHistory/{id}")
    public String getAllHistories(@PathVariable Integer id, Model model){

        try {
        var person = personService.findById(id);
        var historiesOfPerson = medicalHistoryService.findByPatient(person);
        model.addAttribute(HISTORIES_FOR_MODEL, historiesOfPerson);
            return "history/histories-of-patient";
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myMedicalHistory")
    public String getHistoriesOfPatient(Principal principal, Model model) {
        try {
            var  medicalHistories = medicalHistoryService.findHistoriesOfPatient(principal.getName());
        if (medicalHistories.isEmpty()){
                model.addAttribute(IS_PERSON_HAS_HISTORY_FOR_MODEL, false);
            }else {
                model.addAttribute(HISTORIES_FOR_MODEL, medicalHistories);
                model.addAttribute(IS_PERSON_HAS_HISTORY_FOR_MODEL, true);
            }
        return "history/patient-medical-history";
        } catch (PersonException e) {
            model.addAttribute(ERROR_FOR_MODEL,e.getMessage());
            return ERROR_EXCEPTION_PAGE;
        }
    }
}


