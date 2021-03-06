create table roles
(
    id    bigint primary key auto_increment,
    name varchar(512) NOT NULL
);
create table users
(
    id      bigint primary key auto_increment,
    username    varchar(528) NOT NULL UNIQUE ,
    password varchar(528) NOT NULL,
    role_id bigint DEFAULT 2
);

CREATE TABLE persons
(
    id bigint PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(528) NOT NULL ,
    surname VARCHAR(528) NOT NULL ,
    patronymic VARCHAR(528),
    address VARCHAR(528) NOT NULL,
    hire_date DATE ,
    date_of_birthday DATE NOT NULL,
    user_id BIGINT,
    phone_number VARCHAR(528) NOT NULL
);
CREATE TABLE PROCESSES
(
id bigint PRIMARY KEY AUTO_INCREMENT,
name varchar(100) NOT NULL
);
CREATE TABLE NAME_OF_PROCESSES
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name varchar(528) NOT NULL,
    process_id BIGINT NOT NULL
);
CREATE TABLE MEDICAL_HISTORY_PROCESSES
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    quantity int NOT NULL,
    quantity_per_day int,
    name_of_process_id BIGINT NOT NULL,
    status VARCHAR(528) NOT NULL DEFAULT 'false',
    medical_history_id BIGINT NOT NULL
);
CREATE TABLE HISTORY_OF_COMPLETING_PROCESSES
(
id BIGINT PRIMARY KEY AUTO_INCREMENT,
date_of_completing DATE NOT NULL,
medical_history_process_id BIGINT NOT NULL,
personal_id BIGINT NOT NULL
);
CREATE TABLE diagnoses
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        varchar(528) NOT NULL,
    personal_id BIGINT
);

CREATE TABLE medical_history
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    receipt_date DATE NOT NULL ,
    discharge_date DATE,
    patient_id BIGINT,
    diagnosis_id BIGINT,
    status varchar(528) DEFAULT 'false'
);

