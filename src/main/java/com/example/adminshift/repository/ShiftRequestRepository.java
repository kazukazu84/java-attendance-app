package com.example.adminshift.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftRequest;
import com.example.adminshift.entity.ShiftRequestId;

@Repository
public interface ShiftRequestRepository
        extends JpaRepository<ShiftRequest, ShiftRequestId> {
	/**
     * 選択イベントに対する全ユーザーの申請状況取得（Users LEFT JOIN ShiftRequest）
     */
    @Query("SELECT u.userId AS userId, u.userName AS userName, sr.submittedAt AS submittedAt " +
           "FROM Users u " +
           "LEFT JOIN ShiftRequest sr ON u.userId = sr.id.userId AND sr.id.eventId = :eventId " +
           "WHERE u.isActive = 1 " +
           "ORDER BY u.userId ASC")
    List<Object[]> findUserShiftRequestListByEventId(@Param("eventId") Integer eventId);

}
