package com.example.adminshift.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.Shift;

/**
 * シフト情報のデータアクセスを提供するリポジトリ
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    /**
     * 指定されたイベントIDに紐づくシフト一覧を取得します
     *
     * @param eventId イベントID
     * @return シフトのリスト
     */
    List<Shift> findByEventId(Integer eventId);

    /**
     * 指定されたイベントIDに紐づくShiftレコードを一括削除する
     */
    void deleteByEventId(Integer eventId);

    /**
     * イベントID、ユーザーID、勤務日を指定して単一のシフトレコードを取得する
     *
     * @param eventId イベントID
     * @param userId ユーザーID
     * @param shiftDate 勤務日
     * @return シフト情報（存在しない場合はEmpty）
     */
    Optional<Shift> findByEventIdAndUserIdAndShiftDate(Integer eventId, String userId, LocalDate shiftDate);
}