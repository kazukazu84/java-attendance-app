package com.example.adminshift.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftRequestDetail;

@Repository
public interface ShiftRequestDetailRepository
        extends JpaRepository<ShiftRequestDetail, Integer> {

    // --- 追加: イベント期間外の ShiftRequestDetail を削除 ---
    @Modifying
    @Query("DELETE FROM ShiftRequestDetail srd WHERE srd.eventId = :eventId AND (srd.workDate < :startDate OR srd.workDate > :endDate)")
    void deleteByEventIdAndWorkDateOutsideRange(@Param("eventId") Integer eventId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    // --- 追加: イベント期間外になる ShiftRequestDetail が存在するか確認 ---
    @Query("SELECT COUNT(srd) > 0 FROM ShiftRequestDetail srd WHERE srd.eventId = :eventId AND (srd.workDate < :startDate OR srd.workDate > :endDate)")
    boolean existsByEventIdAndWorkDateOutsideRange(@Param("eventId") Integer eventId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}