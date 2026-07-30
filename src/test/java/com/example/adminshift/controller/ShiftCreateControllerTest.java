package com.example.adminshift.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.form.ShiftForm;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.service.ShiftCreateService;


/**
 * ShiftCreateController テストクラス
 *
 * Controller単体テストを行う。
 *
 * ShiftCreateServiceはMockitoでMock化し、
 *
 * ・正しいViewへ遷移するか
 * ・Modelへ必要な値を設定するか
 * ・Serviceが正しく呼ばれるか
 * ・リダイレクトされるか
 *
 * を確認する。
 */
@WebMvcTest(ShiftCreateController.class)
class ShiftCreateControllerTest {


    /**
     * MockMvc
     *
     * ControllerへHTTPリクエストを送信する
     * テスト用クラス
     */
    @Autowired
    private MockMvc mockMvc;



    /**
     * Service Mock
     *
     * 実際のDBアクセスは行わない
     */
    @MockitoBean
    private ShiftCreateService service;
    
    @MockitoBean
    private ShiftRepository shiftRepository;



    /**
     * テスト①
     *
     * 初期表示確認
     *
     * GET
     * /admin/shiftCreate
     */
    @Test
    @WithMockUser
    void 初期表示時にシフト作成画面を表示する()
            throws Exception {


        /*
         * イベント情報
         */
        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);


        when(service.getEventList())
                .thenReturn(List.of(event));


        when(service.getLatestEvent())
                .thenReturn(event);



        /*
         * シフト表表示用データ
         */
        when(service.getCurrentEvent(1))
                .thenReturn(event);


        when(service.getShiftTable(1))
                .thenReturn(List.of());


        when(service.getTargetDateList(event))
                .thenReturn(List.of(
                        LocalDate.of(2026, 8, 1)
                ));



        when(service.getAllUsers())
                .thenReturn(List.of());



        mockMvc.perform(
                get("/admin/shiftCreate")
        )


        .andExpect(status().isOk())


        .andExpect(
                view().name(
                    "admin/shiftCreate"
                )
        )


        .andExpect(
                model().attributeExists(
                    "eventList"
                )
        )


        .andExpect(
                model().attributeExists(
                    "shiftForm"
                )
        );
    }





    /**
     * テスト②
     *
     * イベント変更時
     *
     * POST
     * /admin/shiftCreate/changeEvent
     */
    @Test
    @WithMockUser
    void イベント変更時は選択イベントのシフト表を表示する()
            throws Exception {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(2);



        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(2))
                .thenReturn(event);



        when(service.getShiftTable(2))
                .thenReturn(
                    List.of()
                );


        when(service.getTargetDateList(event))
                .thenReturn(
                    List.of(
                        LocalDate.of(2026,8,1)
                    )
                );


        when(service.getAllUsers())
                .thenReturn(
                    List.of()
                );



        mockMvc.perform(
                post("/admin/shiftCreate/changeEvent")

                .with(csrf())

                .param(
                    "selectedEventId",
                    "2"
                )
        )


        .andExpect(status().isOk())


        .andExpect(
            view().name(
                "admin/shiftCreate"
            )
        )


        .andExpect(
            model().attributeExists(
                "shiftList"
            )
        );
    }





    /**
     * テスト③
     *
     * 既存シフト編集ボタン押下
     *
     * GET
     * /admin/shiftCreate/edit
     */
    @Test
    @WithMockUser
    void 編集押下時は編集フォームを設定する()
            throws Exception {


        Shift shift =
                new Shift();


        shift.setId(1);
        shift.setEventId(1);
        shift.setUserId("user001");
        shift.setShiftDate(
                LocalDate.of(2026,8,1)
        );
        shift.setIsAvailable(1);



        when(service.getShiftDetail(1))
                .thenReturn(
                    shift
                );


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);



        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(1))
                .thenReturn(event);


        when(service.getShiftTable(1))
                .thenReturn(List.of());


        when(service.getTargetDateList(event))
                .thenReturn(List.of());


        when(service.getAllUsers())
                .thenReturn(List.of());



        mockMvc.perform(
                get("/admin/shiftCreate/edit")

                .param(
                    "shiftId",
                    "1"
                )

                .param(
                    "eventId",
                    "1"
                )
        )


        .andExpect(status().isOk())


        .andExpect(
            view().name(
                "admin/shiftCreate"
            )
        )


        .andExpect(
            model().attribute(
                "showModal",
                true
            )
        )


        .andExpect(
            model().attributeExists(
                "shiftForm"
            )
        );
    }
    /**
     * テスト④
     *
     * 新規シフト作成セル押下
     *
     * GET
     * /admin/shiftCreate/new
     */
    @Test
    @WithMockUser
    void 新規作成押下時は新規シフトフォームを設定する()
            throws Exception {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);



        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(1))
                .thenReturn(event);



        when(service.getShiftTable(1))
                .thenReturn(
                    List.of()
                );


        when(service.getTargetDateList(event))
                .thenReturn(
                    List.of(
                        LocalDate.of(2026, 8, 1)
                    )
                );


        when(service.getAllUsers())
                .thenReturn(
                    List.of()
                );



        mockMvc.perform(
                get("/admin/shiftCreate/new")

                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )
        )


        .andExpect(status().isOk())


        .andExpect(
            view().name(
                "admin/shiftCreate"
            )
        )


        .andExpect(
            model().attribute(
                "showModal",
                true
            )
        )


        .andExpect(
            model().attributeExists(
                "shiftForm"
            )
        );
    }





    /**
     * テスト⑤
     *
     * シフト保存成功
     *
     * POST
     * /admin/shiftCreate/update
     */
    @Test
    @WithMockUser
    void シフト保存成功時はリダイレクトする()
            throws Exception {



        ShiftForm form =
                new ShiftForm();


        form.setEventId(1);
        form.setUserId("user001");
        form.setShiftDate(
                LocalDate.of(2026,8,1)
        );


        when(service.saveShift(any(Shift.class)))
                .thenReturn(
                    new Shift()
                );



        mockMvc.perform(

                post("/admin/shiftCreate/update")

                .with(csrf())


                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )

                .param(
                    "startTime",
                    "09:00"
                )

                .param(
                    "endTime",
                    "18:00"
                )

        )


        .andExpect(
            status().is3xxRedirection()
        )


        .andExpect(
            redirectedUrl(
                "/admin/shiftCreate?selectedEventId=1"
            )
        );


        verify(service)
                .saveShift(any(Shift.class));
    }





    /**
     * テスト⑥
     *
     * 休み設定で保存
     *
     * rest=trueの場合
     *
     * isAvailable=0になることを確認
     */
    @Test
    @WithMockUser
    void 休み設定時は休みとして保存する()
            throws Exception {



        when(service.saveShift(any(Shift.class)))
                .thenReturn(
                    new Shift()
                );



        mockMvc.perform(

                post("/admin/shiftCreate/update")

                .with(csrf())


                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )


                /*
                 * 休みチェック
                 */
                .param(
                    "rest",
                    "true"
                )

        )


        .andExpect(
            status().is3xxRedirection()
        );


        verify(service)
                .saveShift(
                    argThat(
                        shift ->
                            Integer.valueOf(0)
                            .equals(
                                shift.getIsAvailable()
                            )
                    )
                );
    }





    /**
     * テスト⑦
     *
     * 戻るボタン押下
     *
     * GET
     * /admin/shiftCreate/back
     */
    @Test
    @WithMockUser
    void 戻るボタン押下時はシフト管理画面へ戻る()
            throws Exception {



        mockMvc.perform(
                get("/admin/shiftCreate/back")
        )


        .andExpect(
            status().is3xxRedirection()
        )


        .andExpect(
            redirectedUrl(
                "/admin/shift-management"
            )
        );
    }
    /**
     * テスト⑧
     *
     * シフト保存時
     * バリデーションエラーの場合
     *
     * 同じ画面へ戻る
     *
     * POST
     * /admin/shiftCreate/update
     */
    @Test
    @WithMockUser
    void シフト保存時にバリデーションエラーの場合は画面再表示する()
            throws Exception {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);



        /*
         * エラー後の画面再表示用データ
         */
        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(1))
                .thenReturn(event);


        when(service.getShiftTable(1))
                .thenReturn(
                    List.of()
                );


        when(service.getTargetDateList(event))
                .thenReturn(
                    List.of()
                );


        when(service.getAllUsers())
                .thenReturn(
                    List.of()
                );



        /*
         * startTimeを未入力にすることで
         * ValidShiftTimeエラーを発生させる
         */
        mockMvc.perform(

                post("/admin/shiftCreate/update")

                .with(csrf())


                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )

                .param(
                    "endTime",
                    "18:00"
                )

        )


        /*
         * redirectではなく
         * 同画面表示
         */
        .andExpect(
            status().isOk()
        )


        .andExpect(
            view().name(
                "admin/shiftCreate"
            )
        )


        /*
         * モーダル表示状態保持確認
         */
        .andExpect(
            model().attribute(
                "showModal",
                true
            )
        );


        /*
         * 保存処理は呼ばれない
         */
        verify(service, never())
                .saveShift(any(Shift.class));

    }





    /**
     * テスト⑨
     *
     * 編集対象シフトが存在しない場合
     *
     * GET
     * /admin/shiftCreate/edit
     *
     * shiftFormは空状態で表示される
     */
    @Test
    @WithMockUser
    void 編集対象シフトが存在しない場合でも画面表示する()
            throws Exception {


        /*
         * 対象シフトなし
         */
        when(service.getShiftDetail(999))
                .thenReturn(null);



        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);



        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(1))
                .thenReturn(event);


        when(service.getShiftTable(1))
                .thenReturn(
                    List.of()
                );


        when(service.getTargetDateList(event))
                .thenReturn(
                    List.of()
                );


        when(service.getAllUsers())
                .thenReturn(
                    List.of()
                );



        mockMvc.perform(

                get("/admin/shiftCreate/edit")

                .param(
                    "shiftId",
                    "999"
                )

                .param(
                    "eventId",
                    "1"
                )
        )


        .andExpect(
            status().isOk()
        )


        .andExpect(
            view().name(
                "admin/shiftCreate"
            )
        )


        .andExpect(
            model().attributeExists(
                "shiftForm"
            )
        );
    }





    /**
     * テスト⑩
     *
     * 保存時に夜勤シフト
     *
     * startTime > endTime
     *
     * の場合でもControllerはServiceへ渡す
     *
     * ※夜勤重複判定はValidator担当
     */
    @Test
    @WithMockUser
    void 夜勤シフト保存時はServiceへ渡す()
            throws Exception {


        when(service.saveShift(any(Shift.class)))
                .thenReturn(
                    new Shift()
                );



        mockMvc.perform(

                post("/admin/shiftCreate/update")

                .with(csrf())


                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )

                /*
                 * 夜勤
                 */
                .param(
                    "startTime",
                    "22:00"
                )

                .param(
                    "endTime",
                    "06:00"
                )

        )


        .andExpect(
            status().is3xxRedirection()
        );


        verify(service)
                .saveShift(any(Shift.class));
    }





    /**
     * テスト⑪
     *
     * createNewShiftで
     * 初期値が正しく設定されることを確認
     */
    @Test
    @WithMockUser
    void 新規作成時は初期値が設定される()
            throws Exception {


        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setEventId(1);



        when(service.getEventList())
                .thenReturn(
                    List.of(event)
                );


        when(service.getCurrentEvent(1))
                .thenReturn(event);


        when(service.getShiftTable(1))
                .thenReturn(
                    List.of()
                );


        when(service.getTargetDateList(event))
                .thenReturn(
                    List.of()
                );


        when(service.getAllUsers())
                .thenReturn(
                    List.of()
                );



        mockMvc.perform(

                get("/admin/shiftCreate/new")

                .param(
                    "eventId",
                    "1"
                )

                .param(
                    "userId",
                    "user001"
                )

                .param(
                    "shiftDate",
                    "2026-08-01"
                )
        )


        .andExpect(
            status().isOk()
        )


        .andExpect(
            model().attribute(
                "showModal",
                true
            )
        )


        .andExpect(
            model().attributeExists(
                "shiftForm"
            )
        );
    }

}
    