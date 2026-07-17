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
@SessionAttributes("CreateShiftApplicationEventForm")
public class ShiftApplicationEventController {
	
	private final ShiftApplicationEventService service;

	private static final String REDIRECT_URL =
			"redirect:/admin/shift-application-event";

	private static final String VIEW_NAME =
			"admin/shift-application-event";

	public ShiftApplicationEventController(
			ShiftApplicationEventService service) {

		this.service = service;
		
	}


/**
 * 初期表示
 */
	@GetMapping
	public String index(Model model) {

	    model.addAttribute(
	            "eventList",
	            service.getEventList());

	    if (!model.containsAttribute(
	            "CreateShiftApplicationEventForm")) {

	        model.addAttribute(
	                "CreateShiftApplicationEventForm",
	                service.getCreateForm());
	    }

	    return VIEW_NAME;
	}

/**
 * 新規作成
 */
@PostMapping("/create")
public String create(
		@Validated
		@ModelAttribute CreateShiftApplicationEventForm form,
		BindingResult bindingResult,
		Model model) {

	if (bindingResult.hasErrors()) {

	    model.addAttribute(
	            "eventList",
	            service.getEventList());

	    model.addAttribute(
	            "CreateShiftApplicationEventForm",
	            form);

	    return VIEW_NAME;
	}

	service.createEvent(form);
	service.saveSetting(form);

	
	return REDIRECT_URL;
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

@PostMapping("/edit")
public String edit(
        @ModelAttribute CreateShiftApplicationEventForm createForm,
        @RequestParam Integer eventId,
        Model model)  {

	ShiftApplicationEvent event = service.getEvent(eventId);

	UpdateShiftApplicationEventForm form = new UpdateShiftApplicationEventForm();

	form.setEventId(event.getEventId());
	form.setTargetStartDate(event.getTargetStartDate());
	form.setTargetEndDate(event.getTargetEndDate());
	form.setApplicationStartDate(event.getApplicationStartDate());
	form.setApplicationEndDate(event.getApplicationEndDate());

	model.addAttribute("editingEventId", eventId);
	model.addAttribute("eventList", service.getEventList());

	model.addAttribute(
	    "UpdateShiftApplicationEventForm",
	    form
	);
	model.addAttribute(
		    "CreateShiftApplicationEventForm",
		    createForm);
	

	return "admin/shift-application-event";
}



}