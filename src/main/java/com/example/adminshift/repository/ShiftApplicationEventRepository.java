package com.example.adminshift.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.ShiftApplicationEvent;

@Repository
public interface ShiftApplicationEventRepository
        extends JpaRepository<ShiftApplicationEvent, Integer> {

    /**
     * 全イベントを eventId の降順で取得（必要に応じてソート指定）
     */
    List<ShiftApplicationEvent> findAllByOrderByEventIdDesc();

    /**
     * eventIdが最も大きい（最新作成）イベントを1件取得
     */
    Optional<ShiftApplicationEvent> findTopByOrderByEventIdDesc();
}