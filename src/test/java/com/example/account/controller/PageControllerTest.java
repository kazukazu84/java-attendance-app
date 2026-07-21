package com.example.account.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Sユーザー画面：GET /user/s-user で account/s-user の View が返ること")
    void suserPage_shouldReturnSuserView() throws Exception {
        mockMvc.perform(get("/user/s-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/s-user"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Mユーザー画面：GET /user/m-user で account/m-user の View が返ること")
    void muserPage_shouldReturnMuserView() throws Exception {
        mockMvc.perform(get("/user/m-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/m-user"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("M管理者画面：GET /admin/m-admin で account/admin/m-admin の View が返ること")
    void madminPage_shouldReturnMadminView() throws Exception {
        mockMvc.perform(get("/admin/m-admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/admin/m-admin"));
    }
}