package com.example.adminshift.controller;

import java.time.LocalDate;
import java.util.List;

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
@RequestMapping("/shift/shiftCreate")
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
    public String index(@ModelAttribute("searchForm") ShiftSearchForm searchForm, Model model) {
        List<ShiftApplicationEvent> eventList = shiftCreateService.getEventList();
        model.addAttribute("eventList", eventList);

        // イベントが1つ以上存在し、未選択の場合は eventId が最も大きい（最新作成）イベントを初期選択とする
        if (searchForm.getSelectedEventId() == null) {
            ShiftApplicationEvent latestEvent = shiftCreateService.getLatestEvent();
            if (latestEvent != null) {
                searchForm.setSelectedEventId(latestEvent.getEventId());
            }
        }

        if (searchForm.getSelectedEventId() != null) {
            setupShiftTableData(searchForm.getSelectedEventId(), model);
        }

        // ポップアップ編集用の空フォームをセット
        if (!model.containsAttribute("shiftForm")) {
            model.addAttribute("shiftForm", new ShiftForm());
        }

        return "shift/shiftCreate";
    }

    /**
     * イベント変更処理（プルダウン選択時）
     *
     * @param searchForm 選択されたイベント情報が含まれるフォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @PostMapping("/changeEvent")
    public String changeEvent(@ModelAttribute("searchForm") ShiftSearchForm searchForm, Model model) {
        return index(searchForm, model);
    }

    /**
     * 既存シフトセル押下処理（ポップアップ編集データの取得・編集モード）
     *
     * @param shiftId    選択されたシフトID
     * @param eventId    現在選択中のイベントID
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("shiftId") Integer shiftId,
                       @RequestParam("eventId") Integer eventId,
                       @ModelAttribute("searchForm") ShiftSearchForm searchForm,
                       Model model) {
        
        searchForm.setSelectedEventId(eventId);

        Shift shift = shiftCreateService.getShiftDetail(shiftId);
        ShiftForm shiftForm = new ShiftForm();

        if (shift != null) {
            shiftForm.setId(shift.getId());
            shiftForm.setEventId(shift.getEventId());
            shiftForm.setUserId(shift.getUserId());
            shiftForm.setShiftDate(shift.getShiftDate());
            shiftForm.setStartTime(shift.getStartTime());
            shiftForm.setEndTime(shift.getEndTime());
            shiftForm.setMemo(shift.getMemo());
            // startTimeがnullの場合は「休み」フラグをtrueにセット
            shiftForm.setRest(shift.getStartTime() == null);
        }

        model.addAttribute("shiftForm", shiftForm);
        model.addAttribute("showModal", true); // ポップアップ自動表示フラグ

        return index(searchForm, model);
    }

    /**
     * 空白セル押下処理（新規作成モーダルデータの作成・新規登録モード）
     *
     * @param eventId    選択中のイベントID
     * @param userId     選択されたユーザーID
     * @param shiftDate  選択された勤務日
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping("/new")
    public String createNewShift(@RequestParam("eventId") Integer eventId,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("shiftDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate shiftDate,
                                 @ModelAttribute("searchForm") ShiftSearchForm searchForm,
                                 Model model) {

        searchForm.setSelectedEventId(eventId);

        ShiftForm shiftForm = new ShiftForm();
        shiftForm.setId(null); // 新規作成のためIDはnull
        shiftForm.setEventId(eventId);
        shiftForm.setUserId(userId);
        shiftForm.setShiftDate(shiftDate);
        shiftForm.setStartTime(null);
        shiftForm.setEndTime(null);
        shiftForm.setMemo("");
        shiftForm.setRest(false);

        model.addAttribute("shiftForm", shiftForm);
        model.addAttribute("showModal", true); // ポップアップ自動表示フラグ

        return index(searchForm, model);
    }

    /**
     * シフト保存・更新処理
     */
    @PostMapping("/update")
    public String update(@Validated @ModelAttribute("shiftForm") ShiftForm shiftForm,
                         BindingResult result,
                         @ModelAttribute("searchForm") ShiftSearchForm searchForm,
                         Model model) {

        if (result.hasErrors()) {
            searchForm.setSelectedEventId(shiftForm.getEventId());
            model.addAttribute("showModal", true);
            return "shift/shiftCreate";
        }

        Shift shift = new Shift();
        shift.setId(shiftForm.getId()); // nullの場合は新規登録(Insert)、値がある場合は更新(Update)
        shift.setEventId(shiftForm.getEventId());
        shift.setUserId(shiftForm.getUserId());
        shift.setShiftDate(shiftForm.getShiftDate());

        // 休みフラグがtrueの場合は出退勤時間をnullにする安全性処理
        if (shiftForm.isRest()) {
            shift.setStartTime(null);
            shift.setEndTime(null);
        } else {
            shift.setStartTime(shiftForm.getStartTime());
            shift.setEndTime(shiftForm.getEndTime());
        }

        shift.setMemo(shiftForm.getMemo());

        shiftCreateService.saveShift(shift);

        return "redirect:/shift/shiftCreate?selectedEventId=" + shiftForm.getEventId();
    }

    /**
     * シフト一覧画面に必要な共通データをModelにセットするプライベートメソッド
     */
    private void setupShiftTableData(Integer eventId, Model model) {
        ShiftApplicationEvent currentEvent = shiftCreateService.getCurrentEvent(eventId);
        List<Shift> shiftList = shiftCreateService.getShiftTable(eventId);
        List<LocalDate> dateList = shiftCreateService.getTargetDateList(currentEvent);
        List<Users> userList = shiftCreateService.getAllUsers();

        model.addAttribute("currentEvent", currentEvent);
        model.addAttribute("shiftList", shiftList);
        model.addAttribute("dateList", dateList);
        model.addAttribute("userList", userList);
    }
}

