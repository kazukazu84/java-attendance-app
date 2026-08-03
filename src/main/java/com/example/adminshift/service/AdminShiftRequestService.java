package com.example.adminshift.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    private static final Logger log = LoggerFactory.getLogger(AdminShiftRequestService.class);

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
     * ※ NOT_SUPPORTED を指定することで、DBエラー発生時に
     * トランザクションが「ロールバック専用」に汚染されるのを防ぎます。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ShiftApplicationEventSelectDto> getTargetEvents() {

        LocalDate today = LocalDate.now();

        try {
            List<ShiftApplicationEvent> events =
                    eventRepository.findTargetEventsForAdminList(today);

            return events.stream()
                    .limit(100)
                    .map(e -> new ShiftApplicationEventSelectDto(
                            e.getEventId(),
                            e.getDisplayName()
                    ))
                    .toList();

        } catch (Exception e) {
            // DB障害やテーブル未作成時にも例外を握りつぶして空リストを返し、HTML側の赤文字表示を可能にする
            log.error("イベント一覧の取得に失敗しました。DBまたはテーブルの状態を確認してください。", e);
            return Collections.emptyList();
        }
    }


    /**
     * 初期選択するイベントIDを決定します。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Integer determineDefaultEventId(
            List<ShiftApplicationEventSelectDto> selectDtos) {

        if (selectDtos == null || selectDtos.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();

        try {
            List<Integer> eventIds =
                    selectDtos.stream()
                            .map(ShiftApplicationEventSelectDto::getEventId)
                            .toList();

            List<ShiftApplicationEvent> events =
                    eventRepository.findAllById(eventIds);

            // ① 今日が対象期間内のイベント
            ShiftApplicationEvent currentEvent =
                    events.stream()
                            .filter(e ->
                                    e.getTargetStartDate() != null
                                    && e.getTargetEndDate() != null
                                    && !today.isBefore(e.getTargetStartDate())
                                    && !today.isAfter(e.getTargetEndDate())
                            )
                            .min(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                            .orElse(null);

            if (currentEvent != null) {
                return currentEvent.getEventId();
            }

            // ② 今日以降に対象期間が終了するイベント
            ShiftApplicationEvent futureEvent =
                    events.stream()
                            .filter(e ->
                                    e.getTargetEndDate() != null
                                    && !e.getTargetEndDate().isBefore(today)
                            )
                            .min(Comparator.comparing(ShiftApplicationEvent::getTargetStartDate))
                            .orElse(null);

            if (futureEvent != null) {
                return futureEvent.getEventId();
            }

            // ③ 最後の保険
            return selectDtos.get(0).getEventId();

        } catch (Exception e) {
            log.error("デフォルトイベントIDの決定処理でエラーが発生しました。", e);
            return selectDtos.get(0).getEventId();
        }
    }


    /**
     * イベントIDに紐づく申請状況一覧を取得します。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ShiftRequestUserSummaryDto> getUserSummaryList(Integer eventId) {

        if (eventId == null) {
            return List.of();
        }

        try {
            List<Object[]> results =
                    shiftRequestRepository.findUserShiftRequestListByEventId(eventId);

            return results.stream()
                    .map(row -> new ShiftRequestUserSummaryDto(
                            (String) row[0],
                            (String) row[1],
                            (java.time.LocalDateTime) row[2]
                    ))
                    .toList();

        } catch (Exception e) {
            log.error("ユーザー申請一覧の取得に失敗しました。eventId={}", eventId, e);
            return List.of();
        }
    }


    /**
     * ユーザーおよびイベントIDに紐づく詳細明細一覧を取得します。
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<ShiftRequestDetailDto> getRequestDetails(String userId, Integer eventId) {

        if (userId == null || eventId == null) {
            return List.of();
        }

        try {
            return detailRepository
                    .findByUserIdAndEventIdOrderByWorkDateAsc(userId, eventId)
                    .stream()
                    .map(d -> new ShiftRequestDetailDto(
                            d.getWorkDate(),
                            d.getIsAvailable(),
                            d.getRequestedStartTime(),
                            d.getRequestedEndTime()
                    ))
                    .toList();

        } catch (Exception e) {
            log.error("申請詳細の取得に失敗しました。userId={}, eventId={}", userId, eventId, e);
            return List.of();
        }
    }
}