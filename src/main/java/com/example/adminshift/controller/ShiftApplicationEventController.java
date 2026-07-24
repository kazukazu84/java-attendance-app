package com.example.adminshift.controller;

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

    @GetMapping
    public String index(Model model) {
        model.addAttribute("eventList", service.getEventList());
        return VIEW_NAME;
    }

    /**
     * 新規作成
     */
    @PostMapping("/create")
    public String create(
            @Validated @ModelAttribute("createShiftApplicationEventForm") CreateShiftApplicationEventForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に不備があります。設定を確認してください。");
            model.addAttribute("eventList", service.getEventList());
            return VIEW_NAME;
        }

        // 重複チェック実行＆登録
        boolean success = service.createEvent(form);
        if (!success) {
            model.addAttribute("errorMessage", "対象期間が他イベントと重複しています");
            model.addAttribute("eventList", service.getEventList());
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
        model.addAttribute("updateShiftApplicationEventForm", form);

        return VIEW_NAME;
    }

    /**
     * 編集完了（更新）
     */
    @PostMapping("/update")
    public String update(
            @Validated @ModelAttribute("updateShiftApplicationEventForm") UpdateShiftApplicationEventForm form,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に不備があります。日付形式等を確認してください。");
            model.addAttribute("editingEventId", form.getEventId());
            model.addAttribute("eventList", service.getEventList());
            return VIEW_NAME;
        }

        // 重複チェック実行＆更新
        boolean success = service.updateEvent(form);
        if (!success) {
            model.addAttribute("errorMessage", "対象期間が他イベントと重複しています");
            model.addAttribute("editingEventId", form.getEventId());
            model.addAttribute("eventList", service.getEventList());
            return VIEW_NAME;
        }

        return REDIRECT_URL;
    }

    /**
     * 削除
     */
    @PostMapping("/delete")
    public String delete(
            @ModelAttribute UpdateShiftApplicationEventForm form) {

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