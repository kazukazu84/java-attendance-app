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
// 属性名を「頭小文字」に統一します
@SessionAttributes("createShiftApplicationEventForm") 
public class ShiftApplicationEventController {

    private final ShiftApplicationEventService service;

    private static final String REDIRECT_URL = "redirect:/admin/shift-application-event";
    private static final String VIEW_NAME = "admin/shift-application-event";

    public ShiftApplicationEventController(ShiftApplicationEventService service) {
        this.service = service;
    }

    /**
     * フォームの初期化（セッションにない場合に呼ばれます）
     */
    @ModelAttribute("createShiftApplicationEventForm")
    public CreateShiftApplicationEventForm setUpCreateForm() {
        return service.getCreateForm();
    }

    /**
     * 初期表示
     */
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
            model.addAttribute("eventList", service.getEventList());
            return VIEW_NAME;
        }

        service.createEvent(form);
        service.saveSetting(form);

        // 登録後も「最後に選択した設定」を画面に残したい場合は、ここでのセッションクリア（status.setComplete()）を行わずリダイレクトします。
        // もしDB等の初期設定値に戻したい場合は status.setComplete() を呼び出してください。

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
 * 編集完了
 */
@PostMapping("/update")
public String update(
        @Validated
        @ModelAttribute UpdateShiftApplicationEventForm form,
        BindingResult bindingResult,
        Model model) {

    if (bindingResult.hasErrors()) {

        model.addAttribute(
                "eventList",
                service.getEventList());

        model.addAttribute(
                "CreateShiftApplicationEventForm",
                service.getCreateForm());

        return VIEW_NAME;
    }

    service.updateEvent(form);

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