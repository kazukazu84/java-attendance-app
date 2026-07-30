package com.example.adminshift.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.adminshift.service.ShiftManagementService;

@WebMvcTest(ShiftManagementController.class)
class ShiftManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;




    @MockitoBean
    private ShiftManagementService shiftManagementService;

    @WithMockUser
    @Test
    void index_正常に画面表示できる() throws Exception {

        mockMvc.perform(get("/admin/shift-management"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/shiftManagement"))
                .andExpect(model().attributeExists("shiftManagementForm"));
  
    }
}

