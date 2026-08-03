package com.example.adminshift.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.adminshift.dto.MonthlyShiftSummaryDto;
import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.service.ShiftCreateService;


@WebMvcTest(ShiftCreateController.class)
class ShiftCreateControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ShiftCreateService shiftCreateService;
    
    @MockBean
    private ShiftRepository shiftRepository;



    /**
     * イベント作成
     */
    private ShiftApplicationEvent createEvent() {

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);

        event.setTargetStartDate(
                LocalDate.of(2026, 8, 1));

        event.setTargetEndDate(
                LocalDate.of(2026, 8, 3));

        event.setApplicationStartDate(
                LocalDate.of(2026, 7, 1));

        event.setApplicationEndDate(
                LocalDate.of(2026, 7, 20));

        return event;
    }



    /**
     * ユーザー作成
     */
    private Users createUser() {

        Users user =
                new Users();

        user.setUserId("user001");

        return user;
    }



    /**
     * シフト作成
     */
    private Shift createShift() {

        Shift shift =
                new Shift();

        shift.setId(1);
        shift.setEventId(1);
        shift.setUserId("user001");
        shift.setShiftDate(
                LocalDate.of(2026, 8, 1));

        shift.setIsAvailable(1);

        return shift;
    }



    /**
     * 月別集計
     */
    private Map<String, List<MonthlyShiftSummaryDto>>
    createSummaryMap() {

        return new HashMap<>();
    }




    @Test
    @DisplayName("初期表示")
    @WithMockUser(roles = "ADMIN")
    void index() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getOldestEvent())
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        List.of(createShift()));


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026,8,1),
                                LocalDate.of(2026,8,2),
                                LocalDate.of(2026,8,3)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(createUser()));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                get("/admin/shiftCreate"))
                .andExpect(status().isOk())
                .andExpect(
                        view()
                        .name("admin/shiftCreate"))
                .andExpect(
                        model()
                        .attributeExists("eventList"))
                .andExpect(
                        model()
                        .attributeExists("shiftForm"));



        verify(shiftCreateService)
                .getEventList();

    }




    @Test
    @DisplayName("イベント変更")
    @WithMockUser(roles = "ADMIN")
    void changeEvent() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        new HashMap<>());



        mockMvc.perform(
                post("/admin/shiftCreate/changeEvent")
                .with(csrf())
                .param(
                    "selectedEventId",
                    "1"))
                .andExpect(status().isOk())
                .andExpect(
                        view()
                        .name("admin/shiftCreate"))
                .andExpect(
                        model()
                        .attributeExists("eventList"));

    }




    @Test
    @DisplayName("戻るボタン")
    @WithMockUser(roles = "ADMIN")
    void back() throws Exception {


        mockMvc.perform(
                get("/admin/shiftCreate/back"))
                .andExpect(
                        status()
                        .is3xxRedirection())
                .andExpect(
                        redirectedUrl(
                        "/admin/shift-management"));

    }@Test
    @DisplayName("既存シフト編集表示")
    @WithMockUser(roles = "ADMIN")
    void edit() throws Exception {


        Shift shift = createShift();


        when(shiftCreateService.getShiftDetail(1))
                .thenReturn(shift);


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        List.of(shift));


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026, 8, 1)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(createUser()));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                get("/admin/shiftCreate/edit")
                .param("shiftId", "1")
                .param("eventId", "1"))
                .andExpect(status().isOk())
                .andExpect(
                        view()
                        .name("admin/shiftCreate"))
                .andExpect(
                        model()
                        .attributeExists("shiftForm"))
                .andExpect(
                        model()
                        .attribute(
                                "showModal",
                                true));



        verify(shiftCreateService)
                .getShiftDetail(1);

    }
    @Test
    @DisplayName("新規シフト作成")
    @WithMockUser(roles = "ADMIN")
    void createNewShift() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026, 8, 1)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(createUser()));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                get("/admin/shiftCreate/new")
                .param("eventId", "1")
                .param("userId", "user001")
                .param("shiftDate", "2026-08-01"))
                .andExpect(status().isOk())
                .andExpect(
                        view()
                        .name("admin/shiftCreate"))
                .andExpect(
                        model()
                        .attributeExists("shiftForm"))
                .andExpect(
                        model()
                        .attribute(
                                "showModal",
                                true));


    }
    @Test
    @DisplayName("シフト更新_正常保存")
    @WithMockUser(roles = "ADMIN")
    void updateSuccess() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026, 8, 1)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(createUser()));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                post("/admin/shiftCreate/update")
                .with(csrf())
                .param("id", "1")
                .param("eventId", "1")
                .param("userId", "user001")
                .param("shiftDate", "2026-08-01")
                .param("startTime", "09:00")
                .param("endTime", "18:00")
                .param("memo", "通常勤務")
                .param("rest", "false"))
                .andExpect(
                        status()
                        .is3xxRedirection())
                .andExpect(
                        redirectedUrl(
                        "/admin/shiftCreate?selectedEventId=1"));



        verify(shiftCreateService)
                .saveShift(any(Shift.class));

    }
    @Test
    @DisplayName("シフト更新_休み保存")
    @WithMockUser(roles = "ADMIN")
    void updateRest() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026, 8, 1)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(createUser()));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                post("/admin/shiftCreate/update")
                .with(csrf())
                .param("eventId", "1")
                .param("userId", "user001")
                .param("shiftDate", "2026-08-01")
                .param("memo", "")
                .param("rest", "true"))
                .andExpect(
                        status()
                        .is3xxRedirection());



        verify(shiftCreateService)
                .saveShift(
                        argThat(shift ->
                                shift.getIsAvailable()
                                .equals(0)));
    }
    @Test
    @DisplayName("シフト更新_バリデーションエラー")
    @WithMockUser(roles = "ADMIN")
    void updateValidationError() throws Exception {


        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        new ArrayList<>());


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        new HashMap<>());



        mockMvc.perform(
                post("/admin/shiftCreate/update")
                .with(csrf())
                .param("eventId", "1")
                .param("userId", "user001")
                .param("shiftDate", "2026-08-01")
                .param("startTime", "")
                .param("endTime", "")
                .param("memo", ""))
                .andExpect(
                        status()
                        .isOk())
                .andExpect(
                        view()
                        .name("admin/shiftCreate"));



        verify(
                shiftCreateService,
                never())
                .saveShift(any(Shift.class));

    }
    @Test
    @DisplayName("シフト表表示_提出状況Map確認")
    @WithMockUser(roles = "ADMIN")
    void indexUserSubmissionMap() throws Exception {


        Shift shift = createShift();


        Users user = createUser();



        when(shiftCreateService.getEventList())
                .thenReturn(
                        List.of(createEvent()));


        when(shiftCreateService.getOldestEvent())
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getCurrentEvent(1))
                .thenReturn(
                        createEvent());


        when(shiftCreateService.getShiftTable(1))
                .thenReturn(
                        List.of(shift));


        when(shiftCreateService.getTargetDateList(any()))
                .thenReturn(
                        List.of(
                                LocalDate.of(2026,8,1)
                        ));


        when(shiftCreateService.getAllUsers())
                .thenReturn(
                        List.of(user));


        when(shiftCreateService.getMonthlySummaryMap(any(), any()))
                .thenReturn(
                        createSummaryMap());



        mockMvc.perform(
                get("/admin/shiftCreate"))
                .andExpect(
                        status().isOk())
                .andExpect(
                        model()
                        .attributeExists(
                        "userSubmissionMap"));



    }
    @Test
    @DisplayName("シフト更新_保存内容確認")
    @WithMockUser(roles = "ADMIN")
    void updateSaveContentCheck() throws Exception {


        mockMvc.perform(
                post("/admin/shiftCreate/update")
                .with(csrf())
                .param("eventId", "1")
                .param("userId", "user001")
                .param("shiftDate", "2026-08-01")
                .param("startTime", "09:00")
                .param("endTime", "18:00")
                .param("memo", "確認用")
                )
                .andExpect(
                        status()
                        .is3xxRedirection());



        ArgumentCaptor<Shift> captor =
                ArgumentCaptor.forClass(
                        Shift.class);



        verify(shiftCreateService)
                .saveShift(
                        captor.capture());



        Shift savedShift =
                captor.getValue();



        assertEquals(
                1,
                savedShift.getEventId());


        assertEquals(
                "user001",
                savedShift.getUserId());


        assertEquals(
                LocalDate.of(2026,8,1),
                savedShift.getShiftDate());


        assertEquals(
                1,
                savedShift.getIsAvailable());

    }
    

}