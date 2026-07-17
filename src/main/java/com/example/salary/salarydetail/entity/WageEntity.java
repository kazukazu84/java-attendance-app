package com.example.salary.salarydetail.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WageEntity {

    @Id
    private int wageId;
    private int wageValue;

}