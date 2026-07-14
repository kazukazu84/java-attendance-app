package com.example.attendance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    // 特定のユーザーの指定日のデータを取得
    Optional<Attendance> findByUserIdAndWorkDate(Integer userId, LocalDate workDate);
}