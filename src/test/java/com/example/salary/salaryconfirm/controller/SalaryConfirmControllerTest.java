package com.example.salary.salaryconfirm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SalaryConfirmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShowConfirmForm() throws Exception {

        mockMvc.perform(get("/salary/confirm"))
                .andExpect(status().isOk())
                .andExpect(view().name("salaryConfirm"))
                .andExpect(model().attributeExists("salaryConfirmForm"))
                .andExpect(model().attributeExists("yearList"));
    }

    @Test
    void testConfirmSalary() throws Exception {

        mockMvc.perform(post("/salary/confirm")
                .param("userId", "1")
                .param("targetYear", "2024"))
                .andExpect(status().isOk())
                .andExpect(view().name("salaryConfirm"))
                .andExpect(model().attributeExists("salaryList"))
                .andExpect(model().attributeExists("totalWorkingHours"))
                .andExpect(model().attributeExists("totalNetSalary"));
    }
}
