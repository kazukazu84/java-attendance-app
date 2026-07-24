package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.dto.GapInfo;
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
     * 【共通ロジック】引数のイベント一覧からGap（未作成期間）を判定
     */
    public List<GapInfo> findGaps(List<ShiftApplicationEvent> events) {
        List<GapInfo> gapList = new ArrayList<>();
        if (events == null || events.size() < 2) {
            return gapList;
        }

        // 開始日の昇順でソート
        List<ShiftApplicationEvent> sorted = events.stream()
                .filter(e -> e.getTargetStartDate() != null && e.getTargetEndDate() != null)
                .sorted(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                .toList();

        for (int i = 0; i < sorted.size() - 1; i++) {
            ShiftApplicationEvent prev = sorted.get(i);
            ShiftApplicationEvent next = sorted.get(i + 1);

            LocalDate gapStart = prev.getTargetEndDate().plusDays(1);
            LocalDate gapEnd = next.getTargetStartDate().minusDays(1);

            // 前イベントの翌日 <= 次イベントの前日 であればGapが存在
            if (!gapStart.isAfter(gapEnd)) {
                gapList.add(new GapInfo(gapStart, gapEnd));
            }
        }
        return gapList;
    }

    /**
     * 現在のDB状態でのGap一覧を取得（一覧画面用）
     */
    public List<GapInfo> getCurrentGaps() {
        List<ShiftApplicationEvent> allEvents = repository.findAllByOrderByTargetStartDateAsc();
        return findGaps(allEvents);
    }

    /**
     * 【仮想リストによるGap判定】編集/作成中の変更内容をメモリ上で適用した後のGapを判定
     */
    public List<GapInfo> calculateGapsWithSimulation(Integer editingEventId, LocalDate newStart, LocalDate newEnd) {
        List<ShiftApplicationEvent> currentEvents = repository.findAllByOrderByTargetStartDateAsc();
        List<ShiftApplicationEvent> simulatedList = new ArrayList<>();

        if (editingEventId == null) {
            // 新規作成シミュレーション
            simulatedList.addAll(currentEvents);
            ShiftApplicationEvent newEvent = new ShiftApplicationEvent();
            newEvent.setTargetStartDate(newStart);
            newEvent.setTargetEndDate(newEnd);
            simulatedList.add(newEvent);
        } else {
            // 編集シミュレーション
            for (ShiftApplicationEvent event : currentEvents) {
                if (event.getEventId().equals(editingEventId)) {
                    ShiftApplicationEvent updated = new ShiftApplicationEvent();
                    updated.setEventId(event.getEventId());
                    updated.setTargetStartDate(newStart);
                    updated.setTargetEndDate(newEnd);
                    simulatedList.add(updated);
                } else {
                    simulatedList.add(event);
                }
            }
        }

        return findGaps(simulatedList);
    }

    /**
     * 新規作成時の次回イベント開始日・終了日を計算（事前チェック用）
     */
    public LocalDate[] calculateNextEventDates(CreateShiftApplicationEventForm form) {
        ShiftApplicationEvent latest = repository.findTopByOrderByTargetEndDateDesc().orElse(null);
        LocalDate start = (latest == null) ? LocalDate.now() : latest.getTargetEndDate().plusDays(1);
        LocalDate end = start.plusWeeks(form.getTargetWeeks()).minusDays(1);
        return new LocalDate[]{start, end};
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
     * 削除対象データが存在するか判定
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
     * イベント新規作成
     */
    public boolean createEvent(CreateShiftApplicationEventForm form) {
        LocalDate[] dates = calculateNextEventDates(form);
        LocalDate targetStartDate = dates[0];
        LocalDate targetEndDate = dates[1];

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
     * イベント更新
     */
    public boolean updateEvent(UpdateShiftApplicationEventForm form) {
        if (isOverlapping(form.getEventId(), form.getTargetStartDate(), form.getTargetEndDate())) {
            return false;
        }

        ShiftApplicationEvent event = repository.findById(form.getEventId()).orElseThrow();

        LocalDate newStart = form.getTargetStartDate();
        LocalDate newEnd = form.getTargetEndDate();

        shiftRepository.deleteByEventIdAndShiftDateOutsideRange(event.getEventId(), newStart, newEnd);
        shiftRequestDetailRepository.deleteByEventIdAndWorkDateOutsideRange(event.getEventId(), newStart, newEnd);

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
                    newShiftsToCreate.add(shift);
                }
            }
            curr = curr.plusDays(1);
        }

        if (!newShiftsToCreate.isEmpty()) {
            shiftRepository.saveAll(newShiftsToCreate);
        }

        event.setTargetStartDate(newStart);
        event.setTargetEndDate(newEnd);
        event.setApplicationStartDate(form.getApplicationStartDate());
        event.setApplicationEndDate(form.getApplicationEndDate());

        repository.save(event);
        return true;
    }

    public void deleteEvent(Integer eventId) {
        shiftRequestDetailRepository.deleteByEventIdAndWorkDateOutsideRange(eventId, LocalDate.of(9999, 12, 31), LocalDate.of(1000, 1, 1));
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