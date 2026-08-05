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

    /**
     * 指定イベントのシフト一覧取得
     *
     * @param eventId イベントID
     * @return シフト一覧
     */
    List<Shift> findByEventId(Integer eventId);

    /**
     * 指定イベントのシフト削除
     *
     * @param eventId イベントID
     */
    void deleteByEventId(Integer eventId);

    /**
     * 指定イベント・ユーザー・日付のシフト取得
     *
     * @param eventId イベントID
     * @param userId ユーザーID
     * @param shiftDate 勤務日
     * @return シフト
     */
    Optional<Shift> findByEventIdAndUserIdAndShiftDate(
            Integer eventId,
            String userId,
            LocalDate shiftDate);

    /**
     * イベント期間外のシフト削除
     *
     * @param eventId イベントID
     * @param startDate 開始日
     * @param endDate 終了日
     */
    @Modifying
    @Query("""
        DELETE FROM Shift s
         WHERE s.eventId = :eventId
           AND (s.shiftDate < :startDate
             OR s.shiftDate > :endDate)
        """)
    void deleteByEventIdAndShiftDateOutsideRange(
            @Param("eventId") Integer eventId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * イベント期間外シフト存在確認
     *
     * @param eventId イベントID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return 存在する場合true
     */
    @Query("""
        SELECT COUNT(s) > 0
          FROM Shift s
         WHERE s.eventId = :eventId
           AND (s.shiftDate < :startDate
             OR s.shiftDate > :endDate)
        """)
    boolean existsByEventIdAndShiftDateOutsideRange(
            @Param("eventId") Integer eventId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * イベントに登録済みの日付一覧取得
     *
     * @param eventId イベントID
     * @return 日付一覧
     */
    @Query("""
        SELECT DISTINCT s.shiftDate
          FROM Shift s
         WHERE s.eventId = :eventId
        """)
    List<LocalDate> findExistingShiftDatesByEventId(
            @Param("eventId") Integer eventId);

    /**
     * 指定ユーザー・期間内のシフト取得
     *
     * 月間勤務時間集計用
     *
     * @param userId ユーザーID
     * @param startDate 開始日
     * @param endDate 終了日
     * @return シフト一覧
     */
    List<Shift> findByUserIdAndShiftDateBetween(
            String userId,
            LocalDate startDate,
            LocalDate endDate);

    /**
     * 指定期間内の全シフト取得
     *
     * 月間集計用
     *
     * @param startDate 開始日
     * @param endDate 終了日
     * @return シフト一覧
     */
    List<Shift> findByShiftDateBetween(
            LocalDate startDate,
            LocalDate endDate);

    /**
     * 指定ユーザー・勤務日のシフト取得
     *
     * 未来日の勤務時間集計用
     *
     * @param userId ユーザーID
     * @param shiftDate 勤務日
     * @return シフト
     */
    Optional<Shift> findByUserIdAndShiftDate(
            String userId,
            LocalDate shiftDate);

    /**
     * ユーザー申請画面表示用
     */
    List<Shift> findByEventIdAndUserIdOrderByShiftDateAsc(
            Integer eventId,
            String userId);

    /**
     * 指定ユーザー・期間内のシフト取得（日付昇順）
     */
    List<Shift> findByUserIdAndShiftDateBetweenOrderByShiftDateAsc(
            String userId,
            LocalDate startDate,
            LocalDate endDate);
}