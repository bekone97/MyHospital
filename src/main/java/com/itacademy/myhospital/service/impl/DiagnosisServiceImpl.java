package com.itacademy.myhospital.service.impl;

import com.itacademy.myhospital.exception.DiagnosisException;
import com.itacademy.myhospital.model.entity.Diagnosis;
import com.itacademy.myhospital.model.entity.Person;
import com.itacademy.myhospital.model.repository.DiagnosisRepository;
import com.itacademy.myhospital.service.DiagnosisService;
import com.itacademy.myhospital.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    public static final String NO_DIAGNOSIS_WITH_ID_EXCEPTION = "There is no diagnosis with id : ";

    private final DiagnosisRepository diagnosisRepository;
    private final PersonService personService;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository, PersonService personService) {
        this.diagnosisRepository = diagnosisRepository;
        this.personService = personService;
    }

    @Override
    public List<Diagnosis> findAll() {
        return diagnosisRepository.findAll();
    }

    @Override
    public Diagnosis findById(Integer id) throws DiagnosisException {
        var optional = diagnosisRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }else {
            throw new DiagnosisException(NO_DIAGNOSIS_WITH_ID_EXCEPTION +id);
        }
    }

    @Override
    public void saveAndFlush(Diagnosis item) {
        diagnosisRepository.saveAndFlush(item);
    }

    @Override
    public void deleteById(Integer id) throws DiagnosisException {
        if(diagnosisRepository.existsById(id)) {
            diagnosisRepository.deleteById(id);
        }else {
            throw new DiagnosisException(NO_DIAGNOSIS_WITH_ID_EXCEPTION +id);
        }
    }



    @Override
    public List<Diagnosis> findByNameAndPersonal(String name, Person personal)  {
        return diagnosisRepository.findByNameAndPersonal(name,personal);
    }


    @Override
    public List<Diagnosis> findByPersonal(Person personal) {
        return diagnosisRepository.findByPersonal(personal);
    }


    @Override
    public Diagnosis findOrCreateDiagnosis(String diagnosisName, Person personal) {
        var diagnosisList=findByNameAndPersonal(diagnosisName,personal);
               return diagnosisList.stream()
                        .findFirst().orElse(Diagnosis.builder()
                               .name(diagnosisName)
                               .personal(personal)
                               .build());
    }

    public List<Diagnosis> getDiagnosesOfPerson(String username){
        var person =personService.findPersonByUsernameOfUser(username);
        return findByPersonal(person);
    }
}
