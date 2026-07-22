package com.example.adminshift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.adminshift.entity.Event;

/**
 * イベント情報のデータアクセスを提供するリポジトリ
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}