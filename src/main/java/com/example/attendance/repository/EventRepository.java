package com.example.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.Event;

/**
 * イベント情報のデータアクセスを提供するリポジトリ
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}