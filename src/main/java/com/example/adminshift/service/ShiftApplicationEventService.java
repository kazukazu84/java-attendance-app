package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftApplicationSetting;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftApplicationSettingRepository;

@Service
@Transactional
public class ShiftApplicationEventService {

	private final ShiftApplicationEventRepository repository;
	private final ShiftApplicationSettingRepository settingRepository;

	public ShiftApplicationEventService(
	        ShiftApplicationEventRepository repository,
	        ShiftApplicationSettingRepository settingRepository) {

	    this.repository = repository;
	    this.settingRepository = settingRepository;
	}

    /**
     * イベント一覧取得
     */
    public List<ShiftApplicationEvent> getEventList() {

        return repository
                .findTop10ByTargetEndDateGreaterThanEqualOrderByTargetStartDate(
                        LocalDate.now());

    }

    /**
     * イベント新規作成
     */
    public void createEvent(
            CreateShiftApplicationEventForm form) {

        ShiftApplicationEvent latest =
                repository.findTopByOrderByTargetEndDateDesc()
                        .orElse(null);

        LocalDate targetStartDate;

        if (latest == null) {
            targetStartDate = LocalDate.now();
        } else {
            targetStartDate = latest.getTargetEndDate().plusDays(1);
        }

        LocalDate targetEndDate =
                targetStartDate
                        .plusWeeks(form.getTargetWeeks())
                        .minusDays(1);

        ShiftApplicationEvent event =
                new ShiftApplicationEvent();

        event.setTargetStartDate(targetStartDate);
        event.setTargetEndDate(targetEndDate);

        event.setApplicationStartDate(
                targetStartDate.minusDays(form.getApplicationStartDays()));

        event.setApplicationEndDate(
                targetStartDate.minusDays(form.getApplicationEndDays()));

        repository.save(event);

    }

    /**
     * イベント取得
     */
    public ShiftApplicationEvent getEvent(Integer eventId) {

        return repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("イベントが存在しません。"));

    }

    /**
     * イベント更新
     */
    public void updateEvent(
            UpdateShiftApplicationEventForm form) {

        ShiftApplicationEvent event =
                repository.findById(form.getEventId())
                        .orElseThrow();

        event.setTargetStartDate(form.getTargetStartDate());
        event.setTargetEndDate(form.getTargetEndDate());
        event.setApplicationStartDate(form.getApplicationStartDate());
        event.setApplicationEndDate(form.getApplicationEndDate());

        repository.save(event);

    }

    /**
     * イベント削除
     */
    public void deleteEvent(Integer eventId) {

        repository.deleteById(eventId);

    }
    
    public CreateShiftApplicationEventForm
    getCreateForm() {

        ShiftApplicationSetting setting =
                settingRepository.findById(1).orElseThrow();

        CreateShiftApplicationEventForm form =
                new CreateShiftApplicationEventForm();

        form.setTargetWeeks(
                setting.getTargetWeeks());

        form.setApplicationStartDays(
                setting.getApplicationStartDays());

        form.setApplicationEndDays(
                setting.getApplicationEndDays());

        return form;
    }
    
    public void saveSetting(
            CreateShiftApplicationEventForm form) {

        ShiftApplicationSetting setting =
                settingRepository.findById(1).orElseThrow();

        setting.setTargetWeeks(
                form.getTargetWeeks());

        setting.setApplicationStartDays(
                form.getApplicationStartDays());

        setting.setApplicationEndDays(
                form.getApplicationEndDays());

        settingRepository.save(setting);
    }
    
    
}