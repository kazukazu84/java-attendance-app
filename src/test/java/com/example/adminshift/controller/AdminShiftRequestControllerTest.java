package com.example.adminshift.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.adminshift.dto.ShiftApplicationEventSelectDto;
import com.example.adminshift.dto.ShiftRequestDetailDto;
import com.example.adminshift.dto.ShiftRequestUserSummaryDto;
import com.example.adminshift.service.AdminShiftRequestService;


/**
 * AdminShiftRequestControllerのテストクラス
 *
 * Controller単体の動作確認を目的とする。
 *
 * 確認対象：
 * ・シフト申請一覧画面の初期表示
 * ・イベントID指定時の表示
 * ・イベントが存在しない場合の表示
 * ・Ajaxによる詳細データ取得API
 *
 * ServiceはMock化し、ControllerからServiceが正しく呼ばれているか、
 * またModelやレスポンスが正しく設定されているかを確認する。
 */
@WebMvcTest(AdminShiftRequestController.class)
class AdminShiftRequestControllerTest {


    /**
     * ControllerへHTTPリクエストを送信するためのテスト用オブジェクト
     *
     * 実際のブラウザの代わりとして、
     * GETやPOSTリクエストをControllerへ送信できる。
     */
    @Autowired
    private MockMvc mockMvc;


    /**
     * Controllerが利用するServiceのMock
     *
     * 実際のServiceやDB処理は実行せず、
     * テストで指定した戻り値を返す。
     */
    @MockitoBean
    private AdminShiftRequestService adminShiftRequestService;



    /**
     * eventIdを指定せずアクセスした場合のテスト
     *
     * 確認内容：
     * ①イベント一覧を取得する
     * ②取得したイベントから初期選択イベントを決定する
     * ③ユーザー申請状況一覧を取得する
     * ④正しいViewとModelを返す
     *
     * 対象URL：
     * GET /admin/shift-request-list
     */
    @Test
    @WithMockUser
    void eventId未指定の場合は初期イベントを選択して表示する() throws Exception {


        /*
         * Serviceが返却するイベント一覧の準備
         *
         * 実際にはDBから取得するデータだが、
         * ControllerテストではMock Serviceから返す。
         */
        List<ShiftApplicationEventSelectDto> events =
                List.of(
                    new ShiftApplicationEventSelectDto(1, "7月シフト")
                );


        /*
         * ユーザー申請状況一覧
         *
         * 今回は空リストを返すケースとして設定
         */
        List<ShiftRequestUserSummaryDto> summaries =
                List.of();



        /*
         * getTargetEvents()が呼ばれた場合、
         * 上記eventsを返すようMock Serviceを設定
         */
        when(adminShiftRequestService.getTargetEvents())
                .thenReturn(events);



        /*
         * eventId未指定の場合、
         * ControllerはdetermineDefaultEventId()を呼び出すため、
         * 初期選択IDとして1を返す設定
         */
        when(adminShiftRequestService.determineDefaultEventId(events))
                .thenReturn(1);



        /*
         * 選択されたイベントIDに対する
         * ユーザー申請一覧を返す設定
         */
        when(adminShiftRequestService.getUserSummaryList(1))
                .thenReturn(summaries);



        /*
         * 実際にGETリクエストを送信
         *
         * 期待結果：
         * ・HTTPステータス200
         * ・正しいView名
         * ・Modelに必要な値が存在する
         */
        mockMvc.perform(get("/admin/shift-request-list"))

                // 正常表示(HTTP200)であることを確認
                .andExpect(status().isOk())

                // Controllerのreturn値を確認
                .andExpect(view()
                        .name("admin/shift-request-list"))

                // Modelへイベント一覧が設定されていることを確認
                .andExpect(model()
                        .attribute("events", events))

                // 初期選択イベントIDが設定されていることを確認
                .andExpect(model()
                        .attribute("selectedEventId", 1))

                // ユーザー一覧が設定されていることを確認
                .andExpect(model()
                        .attribute("userSummaries", summaries));
    }




    /**
     * URLパラメータでeventIdを指定した場合のテスト
     *
     * 確認内容：
     * ・指定したeventIdがそのまま利用される
     * ・デフォルトイベント決定処理が不要である
     *
     * 対象URL：
     * GET /admin/shift-request-list?eventId=5
     */
    @Test
    @WithMockUser
    void eventId指定時は指定イベントで表示する() throws Exception {


        List<ShiftApplicationEventSelectDto> events =
                List.of(
                    new ShiftApplicationEventSelectDto(5, "8月シフト")
                );


        List<ShiftRequestUserSummaryDto> summaries =
                List.of();



        when(adminShiftRequestService.getTargetEvents())
                .thenReturn(events);


        when(adminShiftRequestService.getUserSummaryList(5))
                .thenReturn(summaries);



        mockMvc.perform(
                get("/admin/shift-request-list")
                .param("eventId", "5")
        )


        // HTTP200確認
        .andExpect(status().isOk())


        // 指定したeventIdがModelへ設定されることを確認
        .andExpect(model()
                .attribute("selectedEventId", 5));
    }




    /**
     * イベント一覧が存在しない場合のテスト
     *
     * 確認内容：
     * ・デフォルトイベント決定処理を行わない
     * ・selectedEventIdがnullになる
     *
     * 対象URL：
     * GET /admin/shift-request-list
     */
    @Test
    @WithMockUser
    void イベントが存在しない場合はselectedEventIdがnullになる()
            throws Exception {


        /*
         * イベントが0件の場合
         */
        when(adminShiftRequestService.getTargetEvents())
                .thenReturn(List.of());



        /*
         * eventId=nullの場合、
         * Serviceは空の一覧を返す想定
         */
        when(adminShiftRequestService.getUserSummaryList(null))
                .thenReturn(List.of());



        mockMvc.perform(
                get("/admin/shift-request-list")
        )


        // 正常表示確認
        .andExpect(status().isOk())


        // selectedEventIdがnullであることを確認
        .andExpect(model()
                .attribute("selectedEventId", nullValue()));
    }




    /**
     * Ajaxによる詳細取得APIのテスト
     *
     * 確認内容：
     * ・userIdとeventIdを受け取れる
     * ・Serviceから取得した詳細一覧をJSONで返す
     *
     * 対象URL：
     * GET /admin/shift-request-list/detail
     */
    @Test
    @WithMockUser
    void 詳細Ajax取得が正常に返る() throws Exception {


        /*
         * Ajaxで返却する詳細情報
         *
         * 今回は空配列を返すケース
         */
        List<ShiftRequestDetailDto> details =
                List.of();



        /*
         * 指定されたユーザーID・イベントIDの場合、
         * 詳細一覧を返すようMock設定
         */
        when(adminShiftRequestService.getRequestDetails("001", 1))
                .thenReturn(details);



        mockMvc.perform(
                get("/admin/shift-request-list/detail")
                .param("userId", "001")
                .param("eventId", "1")
        )


        // HTTP200確認
        .andExpect(status().isOk())


        // JSON配列が返却されることを確認
        .andExpect(jsonPath("$").isArray());
    }
}