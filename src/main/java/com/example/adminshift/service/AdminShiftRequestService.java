package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.adminshift.dto.ShiftApplicationEventSelectDto;
import com.example.adminshift.dto.ShiftRequestDetailDto;
import com.example.adminshift.dto.ShiftRequestUserSummaryDto;
import com.example.adminshift.entity.ShiftApplicationEvent;
import com.example.adminshift.repository.ShiftApplicationEventRepository;
import com.example.adminshift.repository.ShiftRequestDetailRepository;
import com.example.adminshift.repository.ShiftRequestRepository;

@Service
@Transactional(readOnly = true)
public class AdminShiftRequestService {

    private final ShiftApplicationEventRepository eventRepository;
    private final ShiftRequestRepository shiftRequestRepository;
    private final ShiftRequestDetailRepository detailRepository;

    public AdminShiftRequestService(
            ShiftApplicationEventRepository eventRepository,
            ShiftRequestRepository shiftRequestRepository,
            ShiftRequestDetailRepository detailRepository) {
        this.eventRepository = eventRepository;
        this.shiftRequestRepository = shiftRequestRepository;
        this.detailRepository = detailRepository;
    }

    /**
     * 表示対象イベント一覧の取得（最大100件）
     */
    public List<ShiftApplicationEventSelectDto> getTargetEvents() {
        LocalDate today = LocalDate.now();
        List<ShiftApplicationEvent> events = eventRepository.findTargetEventsForAdminList(today);

        return events.stream()
                .limit(100)
                .map(e -> new ShiftApplicationEventSelectDto(e.getEventId(), e.getDisplayName()))
                .toList();
    }

    /**
     * 初期選択イベントIDの決定
     */
    public Integer determineDefaultEventId(List<ShiftApplicationEventSelectDto> selectDtos) {
        if (selectDtos.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        List<ShiftApplicationEvent> events = eventRepository.findAllById(
                selectDtos.stream().map(ShiftApplicationEventSelectDto::getEventId).toList()
        );

        // ① 受付中のうち対象期間開始日が最も早いもの
        Optional<ShiftApplicationEvent> openEvent = events.stream()
                .filter(e -> !today.isBefore(e.getApplicationStartDate()) && !today.isAfter(e.getApplicationEndDate()))
                .min(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate));

        if (openEvent.isPresent()) {
            return openEvent.get().getEventId();
        }

        // ② 受付終了イベントのうち対象期間終了日が現在日に最も近いもの
        Optional<ShiftApplicationEvent> closedEvent = events.stream()
                .filter(e -> today.isAfter(e.getApplicationEndDate()))
                .min(Comparator.comparingLong(e -> Math.abs(java.time.temporal.ChronoUnit.DAYS.between(today, e.getTargetEndDate()))));

        return closedEvent.map(ShiftApplicationEvent::getEventId)
                .orElseGet(() -> selectDtos.get(0).getEventId());
    }

    /**
     * イベントIDに紐づく申請状況一覧を取得
     */
    public List<ShiftRequestUserSummaryDto> getUserSummaryList(Integer eventId) {
        if (eventId == null) {
            return List.of();
        }
        List<Object[]> results = shiftRequestRepository.findUserShiftRequestListByEventId(eventId);

        return results.stream().map(row -> new ShiftRequestUserSummaryDto(
                (String) row[0],
                (String) row[1],
                (java.time.LocalDateTime) row[2]
        )).toList();
    }

    /**
     * ユーザーおよびイベントIDに紐づく詳細明細一覧を取得
     */
    public List<ShiftRequestDetailDto> getRequestDetails(String userId, Integer eventId) {
        return detailRepository.findByUserIdAndEventIdOrderByWorkDateAsc(userId, eventId).stream()
                .map(d -> new ShiftRequestDetailDto(
                        d.getWorkDate(),
                        d.getIsAvailable(),
                        d.getRequestedStartTime(),
                        d.getRequestedEndTime()
                )).toList();
    }
}