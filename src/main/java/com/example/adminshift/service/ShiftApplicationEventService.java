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
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.adminshift.repository.UsersRepository;

@Service
@Transactional
public class ShiftApplicationEventService {

    private final ShiftApplicationEventRepository repository;
    private final ShiftApplicationSettingRepository settingRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftRequestDetailRepository shiftRequestDetailRepository;
    private final UsersRepository userRepository;

    public ShiftApplicationEventService(
            ShiftApplicationEventRepository repository,
            ShiftApplicationSettingRepository settingRepository,
            ShiftRepository shiftRepository,
            ShiftRequestDetailRepository shiftRequestDetailRepository,
            UsersRepository userRepository) {

        this.repository = repository;
        this.settingRepository = settingRepository;
        this.shiftRepository = shiftRepository;
        this.shiftRequestDetailRepository = shiftRequestDetailRepository;
        this.userRepository = userRepository;
    }

    /**
     * 重複チェック共通判定
     */
    public boolean isOverlapping(Integer eventId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        if (eventId == null) {
            return repository.existsOverlappingEvent(startDate, endDate);
        } else {
            return repository.existsOverlappingEventExceptSelf(eventId, startDate, endDate);
        }
    }

    /**
     * 削除対象データ（期間外になるShiftまたはShiftRequestDetail）が存在するか判定
     */
    public boolean hasDataToBeDeleted(Integer eventId, LocalDate newStartDate, LocalDate newEndDate) {
        if (eventId == null || newStartDate == null || newEndDate == null) {
            return false;
        }
        boolean hasShift = shiftRepository.existsByEventIdAndShiftDateOutsideRange(eventId, newStartDate, newEndDate);
        boolean hasRequestDetail = shiftRequestDetailRepository.existsByEventIdAndWorkDateOutsideRange(eventId, newStartDate, newEndDate);
        return hasShift || hasRequestDetail;
    }

    /**
     * イベント一覧取得
     */
    public List<ShiftApplicationEvent> getEventList() {
        return repository.findTop10ByTargetEndDateGreaterThanEqualOrderByTargetStartDate(LocalDate.now());
    }

    /**
     * イベント新規作成およびシフトの自動生成
     */
    public boolean createEvent(CreateShiftApplicationEventForm form) {

        ShiftApplicationEvent latest = repository.findTopByOrderByTargetEndDateDesc().orElse(null);

        LocalDate targetStartDate;
        if (latest == null) {
            targetStartDate = LocalDate.now();
        } else {
            targetStartDate = latest.getTargetEndDate().plusDays(1);
        }

        LocalDate targetEndDate = targetStartDate
                .plusWeeks(form.getTargetWeeks())
                .minusDays(1);

        // 重複チェック
        if (isOverlapping(null, targetStartDate, targetEndDate)) {
            return false;
        }

        ShiftApplicationEvent event = new ShiftApplicationEvent();
        event.setTargetStartDate(targetStartDate);
        event.setTargetEndDate(targetEndDate);
        event.setApplicationStartDate(targetStartDate.minusDays(form.getApplicationStartDays()));
        event.setApplicationEndDate(targetStartDate.minusDays(form.getApplicationEndDays()));

        ShiftApplicationEvent savedEvent = repository.save(event);

        List<Users> users = userRepository.findAll();
        List<Shift> shiftsToCreate = createShiftList(savedEvent, users);
        shiftRepository.saveAll(shiftsToCreate);

        return true;
    }

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

    public ShiftApplicationEvent getEvent(Integer eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("イベントが存在しません。"));
    }

    /**
     * イベント更新（差分更新およびデータ整合性の保持）
     */
    public boolean updateEvent(UpdateShiftApplicationEventForm form) {

        // 1. 重複チェック（自分自身を除外）
        if (isOverlapping(form.getEventId(), form.getTargetStartDate(), form.getTargetEndDate())) {
            return false;
        }

        ShiftApplicationEvent event = repository.findById(form.getEventId()).orElseThrow();

        LocalDate newStart = form.getTargetStartDate();
        LocalDate newEnd = form.getTargetEndDate();

        // 2. 期間外データの削除 (Shift & ShiftRequestDetail)
        shiftRepository.deleteByEventIdAndShiftDateOutsideRange(event.getEventId(), newStart, newEnd);
        shiftRequestDetailRepository.deleteByEventIdAndWorkDateOutsideRange(event.getEventId(), newStart, newEnd);

        // 3. 新規日付に対するShift作成（差分追加）
        List<LocalDate> existingDates = shiftRepository.findExistingShiftDatesByEventId(event.getEventId());
        List<Users> users = userRepository.findAll();
        List<Shift> newShiftsToCreate = new ArrayList<>();

        LocalDate curr = newStart;
        while (!curr.isAfter(newEnd)) {
            if (!existingDates.contains(curr)) {
                for (Users user : users) {
                    Shift shift = new Shift();
                    shift.setEventId(event.getEventId());
                    shift.setUserId(user.getUserId());
                    shift.setShiftDate(curr);
                    shift.setStartTime(null);
                    shift.setEndTime(null);
                    shift.setMemo(null);
                    newShiftsToCreate.add(shift);
                }
            }
            curr = curr.plusDays(1);
        }

        if (!newShiftsToCreate.isEmpty()) {
            shiftRepository.saveAll(newShiftsToCreate);
        }

        // 4. イベント本体の更新
        event.setTargetStartDate(newStart);
        event.setTargetEndDate(newEnd);
        event.setApplicationStartDate(form.getApplicationStartDate());
        event.setApplicationEndDate(form.getApplicationEndDate());

        repository.save(event);
        return true;
    }

    public void deleteEvent(Integer eventId) {
        // Shift, ShiftRequestDetail, Event の削除
        shiftRequestDetailRepository.deleteByEventIdAndWorkDateOutsideRange(eventId, LocalDate.of(9999, 12, 31), LocalDate.of(1000, 1, 1)); // 全削除
        shiftRepository.deleteByEventId(eventId);
        repository.deleteById(eventId);
    }

    public CreateShiftApplicationEventForm getCreateForm() {
        ShiftApplicationSetting setting = settingRepository.findById(1).orElseThrow();
        CreateShiftApplicationEventForm form = new CreateShiftApplicationEventForm();
        form.setTargetWeeks(setting.getTargetWeeks());
        form.setApplicationStartDays(setting.getApplicationStartDays());
        form.setApplicationEndDays(setting.getApplicationEndDays());
        return form;
    }

    public void saveSetting(CreateShiftApplicationEventForm form) {
        ShiftApplicationSetting setting = settingRepository.findById(1).orElseThrow();
        setting.setTargetWeeks(form.getTargetWeeks());
        setting.setApplicationStartDays(form.getApplicationStartDays());
        setting.setApplicationEndDays(form.getApplicationEndDays());
        settingRepository.save(setting);
    }
}