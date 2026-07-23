package com.example.attendance.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // 👈 追加
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser; // 👈 追加
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.attendance.dto.AttendanceDto;
import com.example.attendance.service.AttendanceService;
import com.example.main.service.LogService;

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false) // セキュリティフィルターを透過させてコントローラの検証に集中
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private AttendanceService attendanceService;

    @MockitoBean
    private LogService logService;

    // =========================================================================
    // 1. GET /user/attendance , /admin/attendance (画面表示) テスト
    // =========================================================================
    @Nested
    @DisplayName("GET 画面表示機能のテスト")
    class AttendanceViewTest {

        @Test
        @DisplayName("未ログインの場合、ログイン画面へリダイレクトされること")
        void attendance_notLoggedIn_redirectsToLogin() throws Exception {
            mockMvc.perform(get("/user/attendance"))
                   .andExpect(status().is3xxRedirection())
                   .andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithMockUser(username = "admin1", roles = {"ADMIN"}) // 👑 管理者としてログインした状態をシミュレート
        @DisplayName("管理者が /user/attendance にアクセスした場合、/admin/attendance へリダイレクトされること")
        void attendance_adminAccessUserUrl_redirectsToAdminUrl() throws Exception {
            mockMvc.perform(get("/user/attendance").with(csrf()))
            		.andExpect(status().is3xxRedirection())
            		.andExpect(redirectedUrl("/admin/attendance"));
        }

        @Test
        @WithMockUser(username = "user1", roles = {"USER"}) // 💼 一般ユーザーとしてログインした状態をシミュレート
        @DisplayName("一般ユーザーが /admin/attendance にアクセスした場合、/user/attendance へリダイレクトされること")
        void attendance_userAccessAdminUrl_redirectsToUserUrl() throws Exception {
            mockMvc.perform(get("/admin/attendance").with(csrf()))
            		.andExpect(status().is3xxRedirection())
            		.andExpect(redirectedUrl("/user/attendance"));
        }

        @Test
        @WithMockUser(username = "user1", roles = {"USER"})
        @DisplayName("一般ユーザーが正規URL（/user/attendance）にアクセスした場合、勤怠画面が表示されること")
        void attendance_userAccessUserUrl_success() throws Exception {
            AttendanceDto mockDto = new AttendanceDto();
            when(attendanceService.getStatus("user1")).thenReturn(mockDto);

            mockMvc.perform(get("/user/attendance").with(csrf())) // 🔑 .with(csrf())
            		.andExpect(status().isOk())
            		.andExpect(view().name("attendance"))
            		.andExpect(model().attribute("status", mockDto));

            verify(attendanceService, times(1)).getStatus("user1");
        }

        @Test
        @WithMockUser(username = "admin1", roles = {"ADMIN"})
        @DisplayName("管理者が正規URL（/admin/attendance）にアクセスした場合、勤怠画面が表示されること")
        void attendance_adminAccessAdminUrl_success() throws Exception {
            AttendanceDto mockDto = new AttendanceDto();
            when(attendanceService.getStatus("admin1")).thenReturn(mockDto);

            mockMvc.perform(get("/admin/attendance").with(csrf())) // 🔑 .with(csrf())
            		.andExpect(status().isOk())
            		.andExpect(view().name("attendance"))
            		.andExpect(model().attribute("status", mockDto));

            verify(attendanceService, times(1)).getStatus("admin1");
        }
    }

    // =========================================================================
    // 2. POST /api/attendance/clock-in (出勤処理) テスト
    // =========================================================================
    @Nested
    @DisplayName("POST /api/attendance/clock-in 出勤APIのテスト")
    class ClockInTest {

        @Test
        @DisplayName("未ログインの場合、RuntimeExceptionが発生すること")
        void clockIn_notLoggedIn_throwsException() {
            // Controllerメソッドを直接呼び出して例外の発生を検証
            AttendanceController controller = new AttendanceController();
            
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                controller.clockIn(null);
            });

            assertEquals("ログインセッションが切れています。再ログインしてください。", exception.getMessage());
        }

        @Test
        @WithMockUser(username = "user1", roles = {"USER"}) // 🔑 追加
        @DisplayName("ログイン済みの場合、出勤処理が正しく実行されDTOが返却されること")
        void clockIn_loggedIn_success() throws Exception {
            AttendanceDto mockDto = new AttendanceDto();
            when(attendanceService.clockIn("user1")).thenReturn(mockDto);

            mockMvc.perform(post("/api/attendance/clock-in"))
                   .andExpect(status().isOk());

            verify(attendanceService, times(1)).clockIn("user1");
        }
    }

    // =========================================================================
    // 3. POST /api/attendance/clock-out (退勤処理) テスト
    // =========================================================================
    @Nested
    @DisplayName("POST /api/attendance/clock-out 退勤APIのテスト")
    class ClockOutTest {

        @Test
        @DisplayName("未ログインの場合、RuntimeExceptionが発生すること")
        void clockOut_notLoggedIn_throwsException() {
            AttendanceController controller = new AttendanceController();

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                controller.clockOut(null);
            });

            assertEquals("ログインセッションが切れています。再ログインしてください。", exception.getMessage());
        }

        @Test
        @WithMockUser(username = "user1", roles = {"USER"}) // 🔑 追加
        @DisplayName("ログイン済みの場合、退勤処理が正しく実行されDTOが返却されること")
        void clockOut_loggedIn_success() throws Exception {
            AttendanceDto mockDto = new AttendanceDto();
            when(attendanceService.clockOut("user1")).thenReturn(mockDto);

            mockMvc.perform(post("/api/attendance/clock-out"))
                   .andExpect(status().isOk());

            verify(attendanceService, times(1)).clockOut("user1");
        }
    }
}