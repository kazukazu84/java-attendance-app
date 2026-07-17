package com.example.salary.salarydetail.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.example.salary.TestSecurityConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class SalaryDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShowDetail() throws Exception {

        mockMvc.perform(get("/salary/detail")
                .param("userId", "1")
                .param("targetYear", "2024")
                .param("targetMonth", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("salaryDetail"))
                .andExpect(model().attributeExists("detail"));
    }
}
