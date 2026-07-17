package com.example.salary.salarydetail.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SalaryDetailServiceTest {

    @Test
    void testGrossSalary() {
        double workingHours = 160.0;
        int hourlyWage = 1180;

        int gross = (int)(workingHours * hourlyWage);

        assertEquals(188800, gross);
    }

    @Test
    void testInsuranceFee() {
        int gross = 188800;

        int insurance = (int)(gross * 0.005);

        assertEquals(944, insurance);
    }

    @Test
    void testNetSalary() {
        int gross = 188800;
        int insurance = 944;

        int net = gross - insurance;

        assertEquals(187856, net);
    }
}
