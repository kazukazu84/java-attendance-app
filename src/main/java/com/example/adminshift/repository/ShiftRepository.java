package com.example.adminshift.repository;

import java.util.List;

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

}