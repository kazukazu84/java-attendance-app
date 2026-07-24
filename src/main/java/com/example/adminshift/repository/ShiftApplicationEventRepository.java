package com.example.adminshift.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    List<ShiftApplicationEvent> findTop10ByTargetEndDateGreaterThanEqualOrderByTargetStartDate(LocalDate today);

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
}