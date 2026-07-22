package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.entity.Shift;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.entity.ShiftApplicationSetting;
import com.example.adminshift.entity.Users;
import com.example.adminshift.form.CreateShiftApplicationEventForm;
import com.example.adminshift.form.UpdateShiftApplicationEventForm;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftApplicationSettingRepository;
import com.example.adminshift.repository.ShiftRepository;
import com.example.adminshift.repository.UsersRepository;

@Service
@Transactional
public class ShiftApplicationEventService {

	private final ShiftApplicationEventRepository repository;
	private final ShiftApplicationSettingRepository settingRepository;
	private final ShiftRepository shiftRepository;
	private final UsersRepository userRepository;

	public ShiftApplicationEventService(
	        ShiftApplicationEventRepository repository,
	        ShiftApplicationSettingRepository settingRepository,
	        ShiftRepository shiftRepository,
	        UsersRepository userRepository) {

	    this.repository = repository;
	    this.settingRepository = settingRepository;
	    this.shiftRepository = shiftRepository;
	    this.userRepository = userRepository;
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
     * イベント新規作成およびシフトの自動生成
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

        // イベントの保存（DB側でIDが採番される）
        ShiftApplicationEvent savedEvent = repository.save(event);

        // 全ユーザー取得
        List<Users> users = userRepository.findAll();

        // シフト生成
        List<Shift> shiftsToCreate = createShiftList(savedEvent, users);

        // ShiftRepository.saveAll() で一括登録
        shiftRepository.saveAll(shiftsToCreate);

    }

    /**
     * 指定されたイベントの対象期間およびユーザー一覧からShiftリストを作成する
     */
    private List<Shift> createShiftList(ShiftApplicationEvent event, List<Users> users) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate start = event.getTargetStartDate();
        LocalDate end = event.getTargetEndDate();

        if (start == null || end == null || users.isEmpty()) {
            return shifts;
        }

        for (Users user : users) {
            LocalDate currentDate = start;
            while (!currentDate.isAfter(end)) {
                Shift shift = new Shift();
                shift.setEventId(event.getEventId());
                shift.setUserId(user.getUserId());
                shift.setShiftDate(currentDate);
                shift.setStartTime(null);
                shift.setEndTime(null);
                shift.setMemo(null);

                shifts.add(shift);
                currentDate = currentDate.plusDays(1);
            }
        }

        return shifts;
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
     * イベント削除（紐づくShiftレコードも連動して自動削除）
     */
    public void deleteEvent(Integer eventId) {

        // 紐づくShiftデータを一括削除
        shiftRepository.deleteByEventId(eventId);

        // イベント自体を削除
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