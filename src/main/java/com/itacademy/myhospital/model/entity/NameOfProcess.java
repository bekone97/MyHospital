package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "name_of_processes")
public class NameOfProcess {
    public NameOfProcess(Process process) {
        this.process = process;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotBlank(message = "Name is required field")
    private String name;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "process_id")
    private Process process;

    @Override
    public String toString() {
        return "NameOfProcess{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
