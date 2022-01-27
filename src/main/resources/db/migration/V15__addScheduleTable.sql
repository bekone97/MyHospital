
CREATE TABLE Appointments
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_of_appointment DATETIME NOT NULL ,
    personal_id BIGINT NOT NULL,
    user_patient_id BIGINT,
    is_engaged TINYINT(1),
    phone_number varchar(528)
);

