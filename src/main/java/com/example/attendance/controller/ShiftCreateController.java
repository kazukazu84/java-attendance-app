package com.example.attendance.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.attendance.entity.Event;
import com.example.attendance.entity.Shift;
import com.example.attendance.form.ShiftForm;
import com.example.attendance.form.ShiftSearchForm;
import com.example.attendance.service.ShiftCreateService;

import lombok.RequiredArgsConstructor;

/**
 * シフト作成画面の遷移・イベント制御を行うコントローラー
 */
@Controller
@RequestMapping("/shift/shiftCreate") // 先頭に「/」を追加
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
        List<Event> eventList = shiftCreateService.getEventList();
        model.addAttribute("eventList", eventList);

        // イベントが1つ以上存在し、未選択の場合は先頭のイベントを初期選択とする
        if (searchForm.getSelectedEventId() == null && !eventList.isEmpty()) {
            searchForm.setSelectedEventId(eventList.get(0).getId());
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
     * シフトセル押下処理（ポップアップ編集データの取得）
     *
     * @param shiftId    選択されたシフトID
     * @param eventId    現在選択中のイベントID
     * @param searchForm 検索フォーム
     * @param model      画面保持モデル
     * @return シフト作成画面パス
     */
    @GetMapping("/edit")
    public String edit(@RequestParam("shiftId") Long shiftId,
                       @RequestParam("eventId") Long eventId,
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
        }

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
        shift.setId(shiftForm.getId());
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
    private void setupShiftTableData(Long eventId, Model model) {
        Event currentEvent = shiftCreateService.getCurrentEvent(eventId);
        List<Shift> shiftList = shiftCreateService.getShiftTable(eventId);

        model.addAttribute("currentEvent", currentEvent);
        model.addAttribute("shiftList", shiftList);
    }
}