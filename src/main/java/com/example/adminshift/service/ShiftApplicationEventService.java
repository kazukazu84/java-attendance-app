package com.example.adminshift.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final DataSource dataSource;

    public ShiftApplicationEventService(
            ShiftApplicationEventRepository repository,
            ShiftApplicationSettingRepository settingRepository,
            ShiftRepository shiftRepository,
            ShiftRequestDetailRepository shiftRequestDetailRepository,
            UsersRepository userRepository,
            DataSource dataSource) {

        this.repository = repository;
        this.settingRepository = settingRepository;
        this.shiftRepository = shiftRepository;
        this.shiftRequestDetailRepository = shiftRequestDetailRepository;
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    /**
     * DBテーブル（shift_application_event）が存在するかどうか判定
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean isTableExist() {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "shift_application_event", null);
            if (rs.next()) {
                return true;
            }
            ResultSet rsUpper = conn.getMetaData().getTables(null, null, "SHIFT_APPLICATION_EVENT", null);
            return rsUpper.next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * DBテーブル（shift_application_setting）が存在するかどうか判定
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean isSettingTableExist() {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "shift_application_setting", null);
            if (rs.next()) {
                return true;
            }
            ResultSet rsUpper = conn.getMetaData().getTables(null, null, "SHIFT_APPLICATION_SETTING", null);
            return rsUpper.next();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * シフト申請設定テーブルおよび初期データ（ID: 1）が存在するかチェック
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean hasValidSetting() {
        if (!isSettingTableExist()) {
            return false;
        }
        try {
            return settingRepository.existsById(1);
        } catch (DataAccessException e) {
            return false;
        }
    }

    /**
     * 【共通ロジック】引数のイベント一覧からGap（未作成期間）を判定
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GapInfo> findGaps(List<ShiftApplicationEvent> events) {
        List<GapInfo> gapList = new ArrayList<>();
        if (events == null || events.isEmpty()) {
            return gapList;
        }

        List<ShiftApplicationEvent> sorted = events.stream()
                .filter(e -> e.getTargetStartDate() != null && e.getTargetEndDate() != null)
                .sorted(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                .toList();

        if (sorted.isEmpty()) {
            return gapList;
        }

        LocalDate today = LocalDate.now();

        ShiftApplicationEvent first = sorted.get(0);
        if (first.getTargetStartDate().isAfter(today)) {
            LocalDate gapStart = today;
            LocalDate gapEnd = first.getTargetStartDate().minusDays(1);
            if (!gapStart.isAfter(gapEnd)) {
                gapList.add(new GapInfo(gapStart, gapEnd));
            }
        }

        for (int i = 0; i < sorted.size() - 1; i++) {
            ShiftApplicationEvent prev = sorted.get(i);
            ShiftApplicationEvent next = sorted.get(i + 1);

            LocalDate rawGapStart = prev.getTargetEndDate().plusDays(1);
            LocalDate gapEnd = next.getTargetStartDate().minusDays(1);

            LocalDate gapStart = rawGapStart.isBefore(today) ? today : rawGapStart;

            if (!gapStart.isAfter(gapEnd)) {
                gapList.add(new GapInfo(gapStart, gapEnd));
            }
        }
        return gapList;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GapInfo> getCurrentGaps() {
        try {
            List<ShiftApplicationEvent> allEvents = repository.findAllByOrderByTargetStartDateAsc();
            return findGaps(allEvents);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<GapInfo> calculateGapsWithSimulation(Integer editingEventId, LocalDate newStart, LocalDate newEnd) {
        try {
            List<ShiftApplicationEvent> currentEvents = repository.findAllByOrderByTargetStartDateAsc();
            List<ShiftApplicationEvent> simulatedList = new ArrayList<>();

            if (editingEventId == null) {
                simulatedList.addAll(currentEvents);
                ShiftApplicationEvent newEvent = new ShiftApplicationEvent();
                newEvent.setTargetStartDate(newStart);
                newEvent.setTargetEndDate(newEnd);
                simulatedList.add(newEvent);
            } else {
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
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public LocalDate[] calculateNextEventDates(CreateShiftApplicationEventForm form) {
        try {
            ShiftApplicationEvent latest = repository.findTopByOrderByTargetEndDateDesc().orElse(null);
            LocalDate start = (latest == null) ? LocalDate.now() : latest.getTargetEndDate().plusDays(1);
            LocalDate end = start.plusWeeks(form.getTargetWeeks()).minusDays(1);
            return new LocalDate[]{start, end};
        } catch (DataAccessException e) {
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusWeeks(form.getTargetWeeks() != null ? form.getTargetWeeks() : 1).minusDays(1);
            return new LocalDate[]{start, end};
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean isOverlapping(Integer eventId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        try {
            if (eventId == null) {
                return repository.existsOverlappingEvent(startDate, endDate);
            } else {
                return repository.existsOverlappingEventExceptSelf(eventId, startDate, endDate);
            }
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean hasDataToBeDeleted(Integer eventId, LocalDate newStartDate, LocalDate newEndDate) {
        if (eventId == null || newStartDate == null || newEndDate == null) {
            return false;
        }
        try {
            boolean hasShift = shiftRepository.existsByEventIdAndShiftDateOutsideRange(eventId, newStartDate, newEndDate);
            boolean hasRequestDetail = shiftRequestDetailRepository.existsByEventIdAndWorkDateOutsideRange(eventId, newStartDate, newEndDate);
            return hasShift || hasRequestDetail;
        } catch (DataAccessException e) {
            return false;
        }
    }

    /**
     * イベント一覧をページネーション付きで取得
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<ShiftApplicationEvent> getEventList(int page) {
        try {
            Pageable pageable = PageRequest.of(page, 10);
            return repository.findByTargetEndDateGreaterThanEqualOrderByTargetStartDateAsc(LocalDate.now(), pageable);
        } catch (DataAccessException e) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page > 0 ? page : 0, 10), 0);
        }
    }

    public boolean createEvent(CreateShiftApplicationEventForm form) {
        try {
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
        } catch (DataAccessException e) {
            return false;
        }
    }

    private List<Shift> createShiftList(ShiftApplicationEvent event, List<Users> users) {
        List<Shift> shifts = new ArrayList<>();
        LocalDate start = event.getTargetStartDate();
        LocalDate end = event.getTargetEndDate();

        if (start == null || end == null || users == null || users.isEmpty()) {
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
    
    /**
     * 新規ユーザー登録時に、
     * 登録日を含むイベントのShiftを自動作成する。
     *
     * 例）
     * 今日：2026/09/20
     * イベント：2026/09/17～2026/09/30
     *
     * ↓
     *
     * 2026/09/17～2026/09/30のShiftを作成する。
     *
     * @param userId 新規登録したユーザーID
     */
    @Transactional
    public void createShiftForNewUser(String userId) {

        LocalDate today = LocalDate.now();

        // 今日を含むイベントのみ取得
        List<ShiftApplicationEvent> events =
                repository.findTargetEventsForAdminList(today);

        if (events.isEmpty()) {
            return;
        }

        List<Shift> shifts = new ArrayList<>();

        for (ShiftApplicationEvent event : events) {

            LocalDate current = event.getTargetStartDate();

            while (!current.isAfter(event.getTargetEndDate())) {

                // 二重登録防止
                if (shiftRepository.findByEventIdAndUserIdAndShiftDate(
                        event.getEventId(),
                        userId,
                        current).isEmpty()) {

                    Shift shift = new Shift();

                    shift.setEventId(event.getEventId());
                    shift.setUserId(userId);
                    shift.setShiftDate(current);

                    // デフォルト値
                    shift.setIsAvailable(1);

                    shifts.add(shift);
                }

                current = current.plusDays(1);
            }
        }

        if (!shifts.isEmpty()) {
            shiftRepository.saveAll(shifts);
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ShiftApplicationEvent getEvent(Integer eventId) {
        return repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("イベントが存在しません。"));
    }

    public boolean updateEvent(UpdateShiftApplicationEventForm form) {
        if (form.getTargetStartDate() != null && form.getTargetEndDate() != null) {
            if (form.getTargetStartDate().isAfter(form.getTargetEndDate())) {
                return false;
            }
        }
        if (form.getApplicationStartDate() != null && form.getApplicationEndDate() != null) {
            if (form.getApplicationStartDate().isAfter(form.getApplicationEndDate())) {
                return false;
            }
        }

        if (isOverlapping(form.getEventId(), form.getTargetStartDate(), form.getTargetEndDate())) {
            return false;
        }

        try {
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
        } catch (DataAccessException e) {
            return false;
        }
    }

    public void deleteEvent(Integer eventId) {
        try {
            shiftRequestDetailRepository.deleteByEventIdAndWorkDateOutsideRange(eventId, LocalDate.of(9999, 12, 31), LocalDate.of(1000, 1, 1));
            shiftRepository.deleteByEventId(eventId);
            repository.deleteById(eventId);
        } catch (DataAccessException e) {
            // 例外発生時はスキップ
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CreateShiftApplicationEventForm getCreateForm() {
        try {
            ShiftApplicationSetting setting = settingRepository.findById(1).orElse(null);
            CreateShiftApplicationEventForm form = new CreateShiftApplicationEventForm();
            if (setting != null) {
                form.setTargetWeeks(setting.getTargetWeeks());
                form.setApplicationStartDays(setting.getApplicationStartDays());
                form.setApplicationEndDays(setting.getApplicationEndDays());
            } else {
                form.setTargetWeeks(1);
                form.setApplicationStartDays(14);
                form.setApplicationEndDays(7);
            }
            return form;
        } catch (DataAccessException e) {
            CreateShiftApplicationEventForm form = new CreateShiftApplicationEventForm();
            form.setTargetWeeks(1);
            form.setApplicationStartDays(14);
            form.setApplicationEndDays(7);
            return form;
        }
    }

    public void saveSetting(CreateShiftApplicationEventForm form) {
        try {
            ShiftApplicationSetting setting = settingRepository.findById(1).orElse(new ShiftApplicationSetting());
            setting.setSettingId(1);
            setting.setTargetWeeks(form.getTargetWeeks());
            setting.setApplicationStartDays(form.getApplicationStartDays());
            setting.setApplicationEndDays(form.getApplicationEndDays());
            settingRepository.save(setting);
        } catch (DataAccessException e) {
            // 例外発生時はスキップ
        }
    }
    
    /**
     * shift_application_setting の初期データを作成する
     *
     * テーブル生成直後など、
     * データが存在しない場合のみ登録する。
     */
    @Transactional
    public void initializeSetting() {

        // ID=1 が既に存在する場合は何もしない
        if (settingRepository.existsById(1)) {
            return;
        }

        ShiftApplicationSetting setting =
                new ShiftApplicationSetting();

        setting.setSettingId(1);

        // デフォルト値
        setting.setTargetWeeks(2);
        setting.setApplicationStartDays(30);
        setting.setApplicationEndDays(14);

        settingRepository.save(setting);
    }
}