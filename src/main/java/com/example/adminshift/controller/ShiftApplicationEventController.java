package com.example.adminshift.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.adminshift.dto.GapInfo;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.service.ShiftApplicationEventService;

@Controller
@RequestMapping("/admin/shift-application-event")
@SessionAttributes("createShiftApplicationEventForm")
public class ShiftApplicationEventController {

    private final ShiftApplicationEventService service;

    private static final String REDIRECT_URL = "redirect:/admin/shift-application-event";
    private static final String VIEW_NAME = "admin/shift-application-event";

    public ShiftApplicationEventController(ShiftApplicationEventService service) {
        this.service = service;
    }

    @ModelAttribute("createShiftApplicationEventForm")
    public CreateShiftApplicationEventForm setUpCreateForm() {
        return service.getCreateForm();
    }

    @ModelAttribute("updateShiftApplicationEventForm")
    public UpdateShiftApplicationEventForm setUpUpdateForm() {
        return new UpdateShiftApplicationEventForm();
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("eventList", service.getEventList());
        model.addAttribute("gapList", service.getCurrentGaps());
        return VIEW_NAME;
    }

    /**
     * 新規作成
     */
    @PostMapping("/create")
    public String create(
            @Validated @ModelAttribute("createShiftApplicationEventForm") CreateShiftApplicationEventForm form,
            BindingResult bindingResult,
            @RequestParam(name = "confirmConfirmed", defaultValue = "false") boolean confirmConfirmed,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に不備があります。");
            model.addAttribute("eventList", service.getEventList());
            model.addAttribute("gapList", service.getCurrentGaps());
            return VIEW_NAME;
        }

        if (!confirmConfirmed) {
            LocalDate[] dates = service.calculateNextEventDates(form);
            List<GapInfo> simulatedGaps = service.calculateGapsWithSimulation(null, dates[0], dates[1]);
            if (!simulatedGaps.isEmpty()) {
                model.addAttribute("pendingFormType", "create");
                model.addAttribute("pendingGapMessage", simulatedGaps.get(0).getMessage());
                model.addAttribute("eventList", service.getEventList());
                model.addAttribute("gapList", service.getCurrentGaps());
                return VIEW_NAME;
            }
        }

        boolean success = service.createEvent(form);
        if (!success) {
            model.addAttribute("errorMessage", "対象期間が他イベントと重複しています");
            model.addAttribute("eventList", service.getEventList());
            model.addAttribute("gapList", service.getCurrentGaps());
            return VIEW_NAME;
        }

        service.saveSetting(form);
        return REDIRECT_URL;
    }

    /**
     * 編集モード切り替え
     */
    @PostMapping("/edit")
    public String edit(
            @ModelAttribute("createShiftApplicationEventForm") CreateShiftApplicationEventForm createForm,
            @RequestParam Integer eventId,
            Model model) {

        ShiftApplicationEvent event = service.getEvent(eventId);

        UpdateShiftApplicationEventForm form = new UpdateShiftApplicationEventForm();
        form.setEventId(event.getEventId());
        form.setTargetStartDate(event.getTargetStartDate());
        form.setTargetEndDate(event.getTargetEndDate());
        form.setApplicationStartDate(event.getApplicationStartDate());
        form.setApplicationEndDate(event.getApplicationEndDate());

        model.addAttribute("editingEventId", eventId);
        model.addAttribute("eventList", service.getEventList());
        model.addAttribute("gapList", service.getCurrentGaps());
        model.addAttribute("updateShiftApplicationEventForm", form);

        return VIEW_NAME;
    }

    /**
     * 編集モードキャンセル（新規追加）
     */
    @PostMapping("/cancel")
    public String cancel() {
        // DB更新を行わずに一覧画面へリダイレクトして編集モードを解除する
        return REDIRECT_URL;
    }

    /**
     * 編集完了（更新）
     */
    @PostMapping("/update")
    public String update(
            @Validated @ModelAttribute("updateShiftApplicationEventForm") UpdateShiftApplicationEventForm form,
            BindingResult bindingResult,
            @RequestParam(name = "confirmConfirmed", defaultValue = "false") boolean confirmConfirmed,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に不備があります。");
            model.addAttribute("editingEventId", form.getEventId());
            model.addAttribute("eventList", service.getEventList());
            model.addAttribute("gapList", service.getCurrentGaps());
            return VIEW_NAME;
        }

        if (!confirmConfirmed) {
            List<GapInfo> simulatedGaps = service.calculateGapsWithSimulation(form.getEventId(), form.getTargetStartDate(), form.getTargetEndDate());
            boolean hasDeleteData = service.hasDataToBeDeleted(form.getEventId(), form.getTargetStartDate(), form.getTargetEndDate());

            if (!simulatedGaps.isEmpty() || hasDeleteData) {
                StringBuilder messageBuilder = new StringBuilder();

                if (!simulatedGaps.isEmpty()) {
                    for (GapInfo gap : simulatedGaps) {
                        messageBuilder.append("【未作成期間】\n").append(gap.getMessage()).append("\n\n");
                    }
                }
                if (hasDeleteData) {
                    messageBuilder.append("対象期間外になるシフトおよびシフト希望データは削除されます。\n\n");
                }
                messageBuilder.append("このまま登録しますか？");

                model.addAttribute("pendingFormType", "update");
                model.addAttribute("pendingGapMessage", messageBuilder.toString());
                model.addAttribute("editingEventId", form.getEventId());
                model.addAttribute("eventList", service.getEventList());
                model.addAttribute("gapList", service.getCurrentGaps());
                return VIEW_NAME;
            }
        }

        boolean success = service.updateEvent(form);
        if (!success) {
            model.addAttribute("errorMessage", "対象期間が他イベントと重複しています");
            model.addAttribute("editingEventId", form.getEventId());
            model.addAttribute("eventList", service.getEventList());
            model.addAttribute("gapList", service.getCurrentGaps());
            return VIEW_NAME;
        }

        return REDIRECT_URL;
    }

    /**
     * 削除
     */
    @PostMapping("/delete")
    public String delete(@ModelAttribute UpdateShiftApplicationEventForm form) {
        service.deleteEvent(form.getEventId());
        return REDIRECT_URL;
    }

    /**
     * 戻る
     */
    @GetMapping("/back")
    public String back() {
        return "redirect:/admin/shift-management";
    }
}