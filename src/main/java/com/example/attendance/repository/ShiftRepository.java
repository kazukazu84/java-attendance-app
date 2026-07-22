package com.example.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.Shift;

/**
 * シフト情報のデータアクセスを提供するリポジトリ
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    /**
     * 指定されたイベントIDに紐づくシフト一覧を取得します
     *
     * @param eventId イベントID
     * @return シフトのリスト
     */
    List<Shift> findByEventId(Long eventId);
}