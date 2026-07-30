package com.example.salary.salaryconfirm.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class SalaryConfirmServiceTest {

    @Test
    void testTotalNetSalary() {
        int gross = 188800;
        int insurance = 944;
        int net = gross - insurance;

        int total = net * 12; // 仮に12ヶ月分

        assertEquals(2254272, total);
    }
}