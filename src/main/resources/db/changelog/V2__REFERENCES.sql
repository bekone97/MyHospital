ALTER TABLE users ADD
    CONSTRAINT users_roles_fk
        FOREIGN KEY (role_id) REFERENCES roles(id);
ALTER TABLE persons ADD
    CONSTRAINT person_user_fk
        FOREIGN KEY (user_id) REFERENCES users(id) ;

ALTER TABLE diagnoses ADD
    CONSTRAINT diagnoses_persons_fk
        FOREIGN KEY (personal_id) REFERENCES persons (id);


ALTER TABLE medical_history ADD
    CONSTRAINT medical_history_patient_fk
        FOREIGN KEY (patient_id) REFERENCES persons(id);

ALTER TABLE medical_history ADD CONSTRAINT history_diagnosis
    FOREIGN KEY (diagnosis_id) REFERENCES diagnoses(id);

ALTER TABLE NAME_OF_PROCESSES ADD CONSTRAINT name_of_process_fk
    FOREIGN KEY (process_id) REFERENCES processes(id);

ALTER TABLE MEDICAL_HISTORY_PROCESSES ADD CONSTRAINT medical_history_process_fk
    FOREIGN KEY (medical_history_id) REFERENCES medical_history(id);

ALTER TABLE MEDICAL_HISTORY_PROCESSES ADD CONSTRAINT medical_history_process_name_of_process_fk
    FOREIGN KEY (name_of_process_id) REFERENCES name_of_processes(id);

ALTER TABLE HISTORY_OF_COMPLETING_PROCESSES ADD CONSTRAINT completing_process_persons_fk
    FOREIGN KEY (personal_id) REFERENCES persons(id);

ALTER TABLE HISTORY_OF_COMPLETING_PROCESSES ADD CONSTRAINT completing_process_medical_history_process_fk
    FOREIGN KEY (medical_history_process_id) REFERENCES medical_history_processes(id);