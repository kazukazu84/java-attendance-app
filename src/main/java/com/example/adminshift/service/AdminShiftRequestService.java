package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

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
     * 表示対象イベント一覧を取得します。
     *
     * 対象期間終了日が今日以降のイベントを対象とします。
     * 申請受付期間は判定しません。
     *
     * 最大100件を表示します。
     *
     * @return 表示対象イベント一覧
     */
    public List<ShiftApplicationEventSelectDto> getTargetEvents() {

        LocalDate today = LocalDate.now();

        /*
         * Repository側で
         *
         * targetEndDate >= today
         *
         * を条件にして取得します。
         *
         * 受付期間は判定しません。
         */
        List<ShiftApplicationEvent> events =
                eventRepository.findTargetEventsForAdminList(today);

        /*
         * 最大100件
         */
        return events.stream()
                .limit(100)
                .map(e -> new ShiftApplicationEventSelectDto(
                        e.getEventId(),
                        e.getDisplayName()
                ))
                .toList();
    }


    /**
     * 初期選択するイベントIDを決定します。
     *
     * 優先順位：
     *
     * ① 今日が対象期間内のイベント
     *    → 対象期間開始日が最も早いイベント
     *
     * ② 今日が対象期間内のイベントがない場合
     *    → 今日以降に対象期間が終了するイベントのうち
     *       対象期間開始日が最も早いイベント
     *
     * ③ 上記に該当しない場合
     *    → イベント一覧の先頭
     *
     * @param selectDtos イベント選択DTO一覧
     * @return 初期選択イベントID
     */
    public Integer determineDefaultEventId(
            List<ShiftApplicationEventSelectDto> selectDtos) {

        if (selectDtos == null || selectDtos.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();

        /*
         * DTOからイベントIDを取得
         */
        List<Integer> eventIds =
                selectDtos.stream()
                        .map(
                                ShiftApplicationEventSelectDto
                                        ::getEventId
                        )
                        .toList();

        /*
         * DBからイベント情報を取得
         */
        List<ShiftApplicationEvent> events =
                eventRepository.findAllById(eventIds);


        /*
         * ============================================
         * ① 今日が対象期間内のイベント
         * ============================================
         *
         * targetStartDate <= today <= targetEndDate
         */
        ShiftApplicationEvent currentEvent =
                events.stream()
                        .filter(e ->
                                e.getTargetStartDate() != null
                                && e.getTargetEndDate() != null
                                && !today.isBefore(
                                        e.getTargetStartDate())
                                && !today.isAfter(
                                        e.getTargetEndDate())
                        )
                        .min(
                                Comparator.comparing(
                                        ShiftApplicationEvent
                                                ::getTargetStartDate
                                )
                        )
                        .orElse(null);

        if (currentEvent != null) {
            return currentEvent.getEventId();
        }


        /*
         * ============================================
         * ② 今日以降に対象期間が終了するイベント
         * ============================================
         *
         * まだ対象期間が終了していないイベントの中から、
         * 対象期間開始日が最も早いものを選択します。
         */
        ShiftApplicationEvent futureEvent =
                events.stream()
                        .filter(e ->
                                e.getTargetEndDate() != null
                                && !e.getTargetEndDate()
                                        .isBefore(today)
                        )
                        .min(
                                Comparator.comparing(
                                        ShiftApplicationEvent
                                                ::getTargetStartDate
                                )
                        )
                        .orElse(null);

        if (futureEvent != null) {
            return futureEvent.getEventId();
        }


        /*
         * ============================================
         * ③ 最後の保険
         * ============================================
         */
        return selectDtos.get(0).getEventId();
    }


    /**
     * イベントIDに紐づく申請状況一覧を取得します。
     *
     * @param eventId イベントID
     * @return ユーザーごとの申請一覧
     */
    public List<ShiftRequestUserSummaryDto> getUserSummaryList(
            Integer eventId) {

        if (eventId == null) {
            return List.of();
        }

        List<Object[]> results =
                shiftRequestRepository
                        .findUserShiftRequestListByEventId(
                                eventId
                        );

        return results.stream()
                .map(row -> new ShiftRequestUserSummaryDto(
                        (String) row[0],
                        (String) row[1],
                        (java.time.LocalDateTime) row[2]
                ))
                .toList();
    }


    /**
     * ユーザーおよびイベントIDに紐づく
     * 詳細明細一覧を取得します。
     *
     * @param userId ユーザーID
     * @param eventId イベントID
     * @return 申請詳細一覧
     */
    public List<ShiftRequestDetailDto> getRequestDetails(
            String userId,
            Integer eventId) {

        return detailRepository
                .findByUserIdAndEventIdOrderByWorkDateAsc(
                        userId,
                        eventId
                )
                .stream()
                .map(d -> new ShiftRequestDetailDto(
                        d.getWorkDate(),
                        d.getIsAvailable(),
                        d.getRequestedStartTime(),
                        d.getRequestedEndTime()
                ))
                .toList();
    }
}