
package com.example.adminshift.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.Users;
import com.example.adminshift.form.ShiftForm;
import com.example.adminshift.form.ShiftSearchForm;
import com.example.adminshift.service.ShiftCreateService;

import lombok.RequiredArgsConstructor;

/**
 * シフト作成画面の遷移・イベント制御を行うコントローラー
 */
@Controller
@RequestMapping("/admin/shiftCreate")
@RequiredArgsConstructor
public class ShiftCreateController {

    private final ShiftCreateService shiftCreateService;


    /**
     * 初期表示画面処理
     *
     * @param searchForm 検索フォーム（イベント選択用）
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping
    public String index(
            @ModelAttribute("searchForm") ShiftSearchForm searchForm,
            Model model) {

        /*
         * イベント一覧を取得
         */
        List<ShiftApplicationEvent> eventList =
                shiftCreateService.getEventList();

        model.addAttribute(
                "eventList",
                eventList
        );


        /*
         * 未選択の場合は最新イベントを初期選択
         */
        if (searchForm.getSelectedEventId() == null) {

            ShiftApplicationEvent latestEvent =
                    shiftCreateService.getLatestEvent();

            if (latestEvent != null) {

                searchForm.setSelectedEventId(
                        latestEvent.getEventId()
                );
            }
        }


        /*
         * イベントが選択されている場合
         * シフト表データを設定
         */
        if (searchForm.getSelectedEventId() != null) {

            setupShiftTableData(
                    searchForm.getSelectedEventId(),
                    model
            );
        }


        /*
         * ポップアップ編集用の空フォーム
         */
        if (!model.containsAttribute("shiftForm")) {

            model.addAttribute(
                    "shiftForm",
                    new ShiftForm()
            );
        }

        return "admin/shiftCreate";
    }


    /**
     * イベント変更処理
     *
     * @param searchForm 選択されたイベント情報
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @PostMapping("/changeEvent")
    public String changeEvent(
            @ModelAttribute("searchForm") ShiftSearchForm searchForm,
            Model model) {

        return index(
                searchForm,
                model
        );
    }


    /**
     * 既存シフトセル押下処理
     *
     * @param shiftId    シフトID
     * @param eventId    イベントID
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping("/edit")
    public String edit(
            @RequestParam("shiftId") Integer shiftId,
            @RequestParam("eventId") Integer eventId,
            @ModelAttribute("searchForm") ShiftSearchForm searchForm,
            Model model) {

        /*
         * 現在選択中のイベントを保持
         */
        searchForm.setSelectedEventId(eventId);


        /*
         * シフト情報を取得
         */
        Shift shift =
                shiftCreateService.getShiftDetail(shiftId);


        /*
         * 編集用フォーム
         */
        ShiftForm shiftForm =
                new ShiftForm();


        if (shift != null) {

            shiftForm.setId(
                    shift.getId()
            );

            shiftForm.setEventId(
                    shift.getEventId()
            );

            shiftForm.setUserId(
                    shift.getUserId()
            );

            shiftForm.setShiftDate(
                    shift.getShiftDate()
            );

            shiftForm.setStartTime(
                    shift.getStartTime()
            );

            shiftForm.setEndTime(
                    shift.getEndTime()
            );

            shiftForm.setMemo(
                    shift.getMemo()
            );

            shiftForm.setIsAvailable(
                    shift.getIsAvailable()
            );

            /*
             * isAvailable == 0
             * → 休み
             */
            shiftForm.setRest(
                    Integer.valueOf(0).equals(
                            shift.getIsAvailable()
                    )
            );
        }


        /*
         * モーダル表示
         */
        model.addAttribute(
                "shiftForm",
                shiftForm
        );

        model.addAttribute(
                "showModal",
                true
        );


        return index(
                searchForm,
                model
        );
    }


    /**
     * 空白セル押下処理
     *
     * @param eventId    イベントID
     * @param userId     ユーザーID
     * @param shiftDate  勤務日
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping("/new")
    public String createNewShift(
            @RequestParam("eventId") Integer eventId,
            @RequestParam("userId") String userId,
            @RequestParam("shiftDate")
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate shiftDate,

            @ModelAttribute("searchForm")
            ShiftSearchForm searchForm,

            Model model) {

        /*
         * 現在選択中のイベント
         */
        searchForm.setSelectedEventId(
                eventId
        );


        /*
         * 新規登録用フォーム
         */
        ShiftForm shiftForm =
                new ShiftForm();

        shiftForm.setId(null);

        shiftForm.setEventId(
                eventId
        );

        shiftForm.setUserId(
                userId
        );

        shiftForm.setShiftDate(
                shiftDate
        );

        shiftForm.setStartTime(null);

        shiftForm.setEndTime(null);

        shiftForm.setMemo("");

        shiftForm.setRest(false);

        /*
         * 初期状態は出勤
         */
        shiftForm.setIsAvailable(1);


        /*
         * モーダル表示
         */
        model.addAttribute(
                "shiftForm",
                shiftForm
        );

        model.addAttribute(
                "showModal",
                true
        );


        return index(
                searchForm,
                model
        );
    }


    /**
     * シフト保存・更新処理
     *
     * @param shiftForm  シフトフォーム
     * @param result     バリデーション結果
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return 遷移先
     */
    @PostMapping("/update")
    public String update(
            @Validated
            @ModelAttribute("shiftForm")
            ShiftForm shiftForm,

            BindingResult result,

            @ModelAttribute("searchForm")
            ShiftSearchForm searchForm,

            Model model) {


        /*
         * バリデーションエラー
         */
        if (result.hasErrors()) {

            searchForm.setSelectedEventId(
                    shiftForm.getEventId()
            );

            model.addAttribute(
                    "showModal",
                    true
            );

            return index(
                    searchForm,
                    model
            );
        }


        /*
         * Shiftエンティティ作成
         */
        Shift shift =
                new Shift();

        shift.setId(
                shiftForm.getId()
        );

        shift.setEventId(
                shiftForm.getEventId()
        );

        shift.setUserId(
                shiftForm.getUserId()
        );

        shift.setShiftDate(
                shiftForm.getShiftDate()
        );

        shift.setStartTime(
                shiftForm.getStartTime()
        );

        shift.setEndTime(
                shiftForm.getEndTime()
        );

        shift.setMemo(
                shiftForm.getMemo()
        );


        /*
         * 休みチェックボックスから
         * isAvailableを決定
         *
         * true  → 0（休み）
         * false → 1（出勤）
         */
        if (shiftForm.isRest()) {

            shift.setIsAvailable(0);

        } else {

            shift.setIsAvailable(1);
        }


        /*
         * 保存
         */
        shiftCreateService.saveShift(
                shift
        );


        /*
         * 選択中イベントを保持してリダイレクト
         */
        return "redirect:/admin/shiftCreate?selectedEventId="
                + shiftForm.getEventId();
    }


    /**
     * 戻るボタン押下処理
     *
     * @return シフト管理画面
     */
    @GetMapping("/back")
    public String back() {

        return "redirect:/admin/shift-management";
    }


    /**
     * シフト表の表示に必要なデータを設定します。
     *
     * @param eventId イベントID
     * @param model   Model
     */
    private void setupShiftTableData(
            Integer eventId,
            Model model) {


        /*
         * 現在のイベント
         */
        ShiftApplicationEvent currentEvent =
                shiftCreateService.getCurrentEvent(
                        eventId
                );


        /*
         * シフト一覧
         */
        List<Shift> shiftList =
                shiftCreateService.getShiftTable(
                        eventId
                );


        /*
         * 対象期間の日付一覧
         */
        List<LocalDate> dateList =
                shiftCreateService.getTargetDateList(
                        currentEvent
                );


        /*
         * ユーザー一覧
         */
        List<Users> userList =
                shiftCreateService.getAllUsers();


        /*
         * ============================================
         * 月間勤務集計
         * ============================================
         *
         * userId
         *   ↓
         * 月別勤務集計
         */
        var monthlySummaryMap =
                shiftCreateService.getMonthlySummaryMap(
                        shiftList,
                        userList
                );


        /*
         * ============================================
         * ユーザーごとの提出状況
         * ============================================
         *
         * true  = 提出済み
         * false = 未提出
         *
         * 現在選択しているイベントの
         * shiftListにユーザーのデータが
         * 1件でも存在すれば提出済みとします。
         */
        Map<String, Boolean> userSubmissionMap =
                new HashMap<>();


        for (Users user : userList) {

            /*
             * ユーザーIDがnullの場合はスキップ
             */
            if (user == null
                    || user.getUserId() == null) {

                continue;
            }


            /*
             * 現在のイベントに
             * ユーザーのシフトが1件でもあれば提出済み
             */
            boolean submitted =
                    shiftList.stream()
                            .anyMatch(
                                    shift ->
                                            user.getUserId()
                                                    .equals(
                                                            shift.getUserId()
                                                    )
                            );


            /*
             * 提出状況をMapへ格納
             */
            userSubmissionMap.put(
                    user.getUserId(),
                    submitted
            );
        }


        /*
         * ============================================
         * Modelへ設定
         * ============================================
         */

        model.addAttribute(
                "currentEvent",
                currentEvent
        );

        model.addAttribute(
                "shiftList",
                shiftList
        );

        model.addAttribute(
                "dateList",
                dateList
        );

        model.addAttribute(
                "userList",
                userList
        );

        model.addAttribute(
                "monthlySummaryMap",
                monthlySummaryMap
        );

        /*
         * ユーザーごとの提出状況
         */
        model.addAttribute(
                "userSubmissionMap",
                userSubmissionMap
        );
    }
}
