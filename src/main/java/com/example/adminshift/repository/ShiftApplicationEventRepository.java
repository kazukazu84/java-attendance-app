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

    List<ShiftApplicationEvent> findAllByOrderByEventIdDesc();

    Optional<ShiftApplicationEvent> findTopByOrderByEventIdDesc();

    // 変更箇所：Pageableを受け取り、Page<ShiftApplicationEvent> を返すメソッドに変更
    Page<ShiftApplicationEvent> findByTargetEndDateGreaterThanEqualOrderByTargetStartDateAsc(LocalDate today, Pageable pageable);

    Optional<ShiftApplicationEvent> findTopByOrderByTargetEndDateDesc();

    // 重複チェック
    @Query("SELECT COUNT(e) > 0 FROM ShiftApplicationEvent e " +
           "WHERE e.targetStartDate <= :endDate AND e.targetEndDate >= :startDate")
    boolean existsOverlappingEvent(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(e) > 0 FROM ShiftApplicationEvent e " +
           "WHERE e.eventId <> :eventId AND e.targetStartDate <= :endDate AND e.targetEndDate >= :startDate")
    boolean existsOverlappingEventExceptSelf(@Param("eventId") Integer eventId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    /**
     * 対象期間開始日の昇順で全イベントを取得
     */
    List<ShiftApplicationEvent> findAllByOrderByTargetStartDateAsc();

    /**
     * 対象期間開始日昇順、同一の場合はイベントID昇順で全イベントを取得
     */
    List<ShiftApplicationEvent> findAllByOrderByTargetStartDateAscEventIdAsc();
    
    /**
     * シフト申請一覧画面のプルダウン用イベント取得（最大100件）
     */
    @Query("SELECT e FROM ShiftApplicationEvent e " +
           "WHERE (e.applicationStartDate <= :today AND e.applicationEndDate >= :today) " +
           "   OR (e.applicationEndDate < :today AND e.targetEndDate >= :today) " +
           "ORDER BY e.targetStartDate ASC, e.eventId ASC")
    List<ShiftApplicationEvent> findTargetEventsForAdminList(@Param("today") LocalDate today);
}