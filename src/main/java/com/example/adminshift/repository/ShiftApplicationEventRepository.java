package com.example.adminshift.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftApplicationEvent;

@Repository
public interface ShiftApplicationEventRepository
        extends JpaRepository<ShiftApplicationEvent, Integer> {
	
	ShiftApplicationEvent findFirstByOrderByEventIdAsc();
	

    /**
     * イベントIDの降順ですべてのイベントを取得
     */
    List<ShiftApplicationEvent> findAllByOrderByEventIdDesc();


    /**
     * イベントIDが最大のイベントを1件取得
     */
    Optional<ShiftApplicationEvent> findTopByOrderByEventIdDesc();


    /**
     * イベントIDが最小のイベントを1件取得
     *
     * 登録されているイベントの中で、
     * イベントIDが一番小さいイベントを取得します。
     */
    Optional<ShiftApplicationEvent> findTopByOrderByEventIdAsc();


    /**
     * 対象期間終了日が今日以降のイベントを、
     * 対象期間開始日の昇順で取得します。
     *
     * Pageableを使用して取得件数を制限します。
     *
     * @param today 基準日
     * @param pageable ページング情報
     * @return 対象期間が終了していないイベント
     */
    Page<ShiftApplicationEvent>
    findByTargetEndDateGreaterThanEqualOrderByTargetStartDateAsc(
            LocalDate today,
            Pageable pageable
    );


    /**
     * 対象期間終了日が最も遅いイベントを取得
     */
    Optional<ShiftApplicationEvent>
    findTopByOrderByTargetEndDateDesc();


    /**
     * 対象期間の重複チェック
     *
     * @param startDate 対象期間開始日
     * @param endDate 対象期間終了日
     * @return 重複するイベントが存在する場合true
     */
    @Query(
        "SELECT COUNT(e) > 0 " +
        "FROM ShiftApplicationEvent e " +
        "WHERE e.targetStartDate <= :endDate " +
        "AND e.targetEndDate >= :startDate"
    )
    boolean existsOverlappingEvent(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    /**
     * 自分自身を除外した対象期間の重複チェック
     *
     * @param eventId 除外するイベントID
     * @param startDate 対象期間開始日
     * @param endDate 対象期間終了日
     * @return 重複するイベントが存在する場合true
     */
    @Query(
        "SELECT COUNT(e) > 0 " +
        "FROM ShiftApplicationEvent e " +
        "WHERE e.eventId <> :eventId " +
        "AND e.targetStartDate <= :endDate " +
        "AND e.targetEndDate >= :startDate"
    )
    boolean existsOverlappingEventExceptSelf(
            @Param("eventId") Integer eventId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    /**
     * 対象期間開始日の昇順で全イベントを取得
     */
    List<ShiftApplicationEvent>
    findAllByOrderByTargetStartDateAsc();


    /**
     * 対象期間開始日昇順、
     * 同一の場合はイベントID昇順で全イベントを取得
     */
    List<ShiftApplicationEvent>
    findAllByOrderByTargetStartDateAscEventIdAsc();


    /**
     * シフト申請一覧画面のプルダウン用イベント取得
     *
     * 対象期間終了日が今日以降のイベントのみを表示します。
     *
     * 受付期間は判定しません。
     *
     * 例：
     *
     * 対象期間
     * 2026/08/01 ～ 2026/08/24
     *
     * 今日が2026/07/30
     * → 表示
     *
     * 今日が2026/08/10
     * → 表示
     *
     * 今日が2026/08/25
     * → 非表示
     *
     * @param today 基準日
     * @return 表示対象イベント一覧
     */
    @Query(
        "SELECT e " +
        "FROM ShiftApplicationEvent e " +
        "WHERE e.targetEndDate >= :today " +
        "ORDER BY e.targetStartDate ASC, e.eventId ASC"
    )
    List<ShiftApplicationEvent> findTargetEventsForAdminList(
            @Param("today") LocalDate today
    );
}