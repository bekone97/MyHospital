package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name= "patronymic")
    private String patronymic;

    @Column(name="address")
    private String address;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "date_of_birthday")
    private LocalDate dateOfBirthday;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.REFRESH})
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "key_for_user")
    private String keyForUser;
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "patient")
    private List<MedicalHistory> histories;


    public Person(String firstName, String surname, String patronymic, String address, LocalDate dateOfBirthday, User user) {
        this.firstName = firstName;
        this.surname = surname;
        this.patronymic = patronymic;
        this.address = address;
        this.dateOfBirthday = dateOfBirthday;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", address='" + address + '\'' +
                ", hireDate=" + hireDate +
                ", dateOfBirthday=" + dateOfBirthday +
                '}';
    }
    public String getFIO(){
        return (this.firstName+" "+this.surname+" "+this.patronymic);
    }
}
