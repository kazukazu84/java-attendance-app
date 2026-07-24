package com.example.adminshift.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.Shift;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    List<Shift> findByEventId(Integer eventId);

    void deleteByEventId(Integer eventId);

    Optional<Shift> findByEventIdAndUserIdAndShiftDate(Integer eventId, String userId, LocalDate shiftDate);

    // --- 追加: イベント期間外（startDateより前 または endDateより後）のShiftを削除 ---
    @Modifying
    @Query("DELETE FROM Shift s WHERE s.eventId = :eventId AND (s.shiftDate < :startDate OR s.shiftDate > :endDate)")
    void deleteByEventIdAndShiftDateOutsideRange(@Param("eventId") Integer eventId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // --- 追加: イベント期間外になるShiftデータが存在するか確認 ---
    @Query("SELECT COUNT(s) > 0 FROM Shift s WHERE s.eventId = :eventId AND (s.shiftDate < :startDate OR s.shiftDate > :endDate)")
    boolean existsByEventIdAndShiftDateOutsideRange(@Param("eventId") Integer eventId,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    // --- 追加: 既存の日付一覧を取得（差分更新用） ---
    @Query("SELECT DISTINCT s.shiftDate FROM Shift s WHERE s.eventId = :eventId")
    List<LocalDate> findExistingShiftDatesByEventId(@Param("eventId") Integer eventId);
}