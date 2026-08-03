package com.example.adminshift.controller;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.service.ShiftApplicationEventService;


/**
 * ShiftApplicationEventController 単体テスト
 *
 * ServiceはMock化し、
 * Controllerの
 *
 * ・画面遷移
 * ・Model設定
 * ・Service呼び出し
 *
 * を確認する。
 */
@WebMvcTest(ShiftApplicationEventController.class)
class ShiftApplicationEventControllerTest {


    private static final String BASE_URL =
            "/admin/shift-application-event";


    private static final String VIEW_NAME =
            "admin/shift-application-event";



    @Autowired
    private MockMvc mockMvc;



    @MockitoBean
    private ShiftApplicationEventService service;



    /**
     * 各テスト共通設定
     *
     * エラー画面再表示などで必要になる
     * Model用データをMock設定する。
     */
    @BeforeEach
    void setup() {


        when(service.getCreateForm())
                .thenReturn(
                    new CreateShiftApplicationEventForm()
                );


        Page<ShiftApplicationEvent> page =
                new PageImpl<>(List.of());


        when(service.getEventList(anyInt()))
                .thenReturn(page);


        when(service.getCurrentGaps())
                .thenReturn(List.of());
    }





    /**
     * テスト①
     *
     * 初期表示確認
     *
     * GET
     * /admin/shift-application-event
     */
    @Test
    @WithMockUser
    void 初期表示でイベント一覧画面を表示する()
            throws Exception {


        Page<ShiftApplicationEvent> page =
                new PageImpl<>(List.of());


        when(service.getEventList(0))
                .thenReturn(page);



        mockMvc.perform(
                get(BASE_URL)
        )


        .andExpect(status().isOk())


        .andExpect(
                view()
                .name(VIEW_NAME)
        )


        .andExpect(
                model()
                .attribute(
                        "eventPage",
                        page
                )
        )


        .andExpect(
                model()
                .attribute(
                        "eventList",
                        List.of()
                )
        );
    }





    /**
     * テスト②
     *
     * ページ番号指定時
     *
     * GET
     * /admin/shift-application-event?page=2
     */
    @Test
    @WithMockUser
    void ページ番号指定時は指定ページを取得する()
            throws Exception {


        Page<ShiftApplicationEvent> page =
                new PageImpl<>(List.of());


        when(service.getEventList(2))
                .thenReturn(page);



        mockMvc.perform(
                get(BASE_URL)
                .param(
                    "page",
                    "2"
                )
        )


        .andExpect(status().isOk())


        .andExpect(
                model()
                .attribute(
                        "eventPage",
                        page
                )
        );
    }
    
    /**
     * テスト③
     *
     * 新規イベント作成成功時
     *
     * POST
     * /admin/shift-application-event/create
     */
    @Test
    @WithMockUser
    void 新規イベント作成成功時はリダイレクトする()
            throws Exception {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();



        when(service.createEvent(
                any(CreateShiftApplicationEventForm.class)
        ))
        .thenReturn(true);



        mockMvc.perform(

                post(BASE_URL + "/create")

                .flashAttr(
                    "createShiftApplicationEventForm",
                    form
                )

                .with(csrf())

                .param(
                    "targetWeeks",
                    "2"
                )

                .param(
                    "applicationStartDays",
                    "30"
                )

                .param(
                    "applicationEndDays",
                    "14"
                )

                .param(
                    "confirmConfirmed",
                    "true"
                )
        )


        .andExpect(
                status().is3xxRedirection()
        )


        .andExpect(
                redirectedUrl(BASE_URL)
        );
    }





    /**
     * テスト④
     *
     * 新規イベント作成時
     * 対象期間重複の場合
     *
     * 同じ画面へ戻る
     */
    @Test
    @WithMockUser
    void 新規イベント作成時に期間重複の場合はエラー表示する()
            throws Exception {


        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();



        when(service.createEvent(
                any(CreateShiftApplicationEventForm.class)
        ))
        .thenReturn(false);



        mockMvc.perform(

                post(BASE_URL + "/create")

                .flashAttr(
                    "createShiftApplicationEventForm",
                    form
                )

                .with(csrf())

                .param(
                    "targetWeeks",
                    "2"
                )

                .param(
                    "applicationStartDays",
                    "30"
                )

                .param(
                    "applicationEndDays",
                    "14"
                )

                .param(
                    "confirmConfirmed",
                    "true"
                )
        )


        .andExpect(
                status().isOk()
        )


        .andExpect(
                view()
                .name(VIEW_NAME)
        )


        .andExpect(
                model()
                .attribute(
                    "errorMessage",
                    "対象期間が他イベントと重複しています"
                )
        );
    }





    /**
     * テスト⑤
     *
     * 編集ボタン押下時
     *
     * 編集フォームを設定して表示する
     *
     * POST
     * /edit
     */
    @Test
    @WithMockUser
    void 編集ボタン押下時は編集フォームを設定して表示する()
            throws Exception {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();


        event.setEventId(1);


        event.setTargetStartDate(
                java.time.LocalDate.of(
                        2026,
                        8,
                        1
                )
        );


        event.setTargetEndDate(
                java.time.LocalDate.of(
                        2026,
                        8,
                        14
                )
        );


        event.setApplicationStartDate(
                java.time.LocalDate.of(
                        2026,
                        7,
                        1
                )
        );


        event.setApplicationEndDate(
                java.time.LocalDate.of(
                        2026,
                        7,
                        18
                )
        );



        when(service.getEvent(1))
                .thenReturn(event);



        mockMvc.perform(

                post(BASE_URL + "/edit")

                .with(csrf())

                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "page",
                    "0"
                )
        )


        .andExpect(
                status().isOk()
        )


        .andExpect(
                view()
                .name(VIEW_NAME)
        )


        .andExpect(
                model()
                .attribute(
                    "editingEventId",
                    1
                )
        )


        .andExpect(
                model()
                .attributeExists(
                    "updateShiftApplicationEventForm"
                )
        );
    }





    /**
     * テスト⑥
     *
     * 編集更新成功時
     *
     * リダイレクトする
     *
     * POST
     * /update
     */
    @Test
    @WithMockUser
    void 編集更新成功時はリダイレクトする()
            throws Exception {


        when(service.updateEvent(
                any(UpdateShiftApplicationEventForm.class)
        ))
        .thenReturn(true);



        mockMvc.perform(

                post(BASE_URL + "/update")

                .with(csrf())


                .param(
                    "eventId",
                    "1"
                )


                .param(
                    "targetStartDate",
                    "2026-08-01"
                )


                .param(
                    "targetEndDate",
                    "2026-08-14"
                )


                .param(
                    "applicationStartDate",
                    "2026-07-01"
                )


                .param(
                    "applicationEndDate",
                    "2026-07-15"
                )


                .param(
                    "confirmConfirmed",
                    "true"
                )
        )


        .andExpect(
                status().is3xxRedirection()
        )


        .andExpect(
                redirectedUrl(
                    BASE_URL + "?page=0"
                )
        );
    }
    
    /**
     * テスト⑦
     *
     * 編集更新失敗時
     *
     * 重複エラーなどで更新できない場合
     * 同じ画面へ戻る
     *
     * POST
     * /admin/shift-application-event/update
     */
    @Test
    @WithMockUser
    void 編集更新失敗時はエラー表示して同じ画面を表示する()
            throws Exception {


        when(service.getCreateForm())
                .thenReturn(
                        new CreateShiftApplicationEventForm()
                );


        /*
         * 更新失敗
         */
        when(service.updateEvent(
                any(UpdateShiftApplicationEventForm.class)
        ))
        .thenReturn(false);



        mockMvc.perform(
                post("/admin/shift-application-event/update")
                .with(csrf())

                .param("eventId", "1")

                .param(
                    "targetStartDate",
                    "2026-08-01"
                )

                .param(
                    "targetEndDate",
                    "2026-08-14"
                )

                .param(
                    "applicationStartDate",
                    "2026-07-01"
                )

                .param(
                    "applicationEndDate",
                    "2026-07-15"
                )

                .param(
                    "confirmConfirmed",
                    "true"
                )
        )


        .andExpect(
                status().isOk()
        )


        .andExpect(
                view().name(
                        "admin/shift-application-event"
                )
        );

    }





    /**
     * テスト⑨
     *
     * 削除成功時
     *
     * Service.deleteEvent()
     * 呼出確認
     */
    @Test
    @WithMockUser
    void 削除成功時はリダイレクトする()
            throws Exception {


        Integer eventId = 1;


        mockMvc.perform(
                post("/admin/shift-application-event/delete")

                .with(csrf())

                .param(
                        "eventId",
                        eventId.toString()
                )

                .param(
                        "page",
                        "2"
                )
        )


        .andExpect(
                status().is3xxRedirection()
        )


        .andExpect(
                redirectedUrl(
                    "/admin/shift-application-event?page=2"
                )
        );


        /*
         * Service呼出確認
         */
        verify(service)
                .deleteEvent(eventId);

    }





    /**
     * テスト⑩
     *
     * 削除時
     *
     * 指定ページを維持して
     * リダイレクトする
     */
    @Test
    @WithMockUser
    void 削除時は指定ページへリダイレクトする()
            throws Exception {


        Integer eventId = 999;


        mockMvc.perform(
                post("/admin/shift-application-event/delete")

                .with(csrf())

                .param(
                        "eventId",
                        eventId.toString()
                )

                .param(
                        "page",
                        "3"
                )
        )


        .andExpect(
                status().is3xxRedirection()
        )


        .andExpect(
                redirectedUrl(
                    "/admin/shift-application-event?page=3"
                )
        );


        verify(service)
                .deleteEvent(eventId);

    }





    /**
     * テスト⑪
     *
     * 編集更新時
     *
     * 開始日 > 終了日の場合
     *
     * エラー表示する
     */
    @Test
    @WithMockUser
    void 編集更新時に日付不正の場合はエラー表示する()
            throws Exception {



        when(service.getCreateForm())
                .thenReturn(
                        new CreateShiftApplicationEventForm()
                );



        mockMvc.perform(
                post("/admin/shift-application-event/update")

                .with(csrf())


                .param(
                        "eventId",
                        "1"
                )


                /*
                 * 不正
                 *
                 * 開始日 > 終了日
                 */
                .param(
                        "targetStartDate",
                        "2026-08-14"
                )

                .param(
                        "targetEndDate",
                        "2026-08-01"
                )


                .param(
                        "applicationStartDate",
                        "2026-07-01"
                )


                .param(
                        "applicationEndDate",
                        "2026-07-15"
                )


                .param(
                        "confirmConfirmed",
                        "true"
                )
        )


        .andExpect(
                status().isOk()
        )


        .andExpect(
                view().name(
                    "admin/shift-application-event"
                )
        )


        .andExpect(
                model().attribute(
                        "errorMessage",
                        "入力内容に不備があります。"
                )
        );

    }
    

    
    

}