package com.example.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.main.entity.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {
    // ログを表示する際、新着順（日時の降順）で取得するためのメソッド
    List<Log> findAllByOrderByCreatedAtDesc();
}

