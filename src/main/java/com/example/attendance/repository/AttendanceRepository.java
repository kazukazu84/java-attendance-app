package com.example.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    Optional<Attendance> findByUserIdAndWorkDate(String userId, LocalDate workDate);

    // ★ 月別勤怠一覧（Between で確実に動く）
    List<Attendance> findByUserIdAndWorkDateBetween(
            String userId,
            LocalDate start,
            LocalDate end
    );
}
