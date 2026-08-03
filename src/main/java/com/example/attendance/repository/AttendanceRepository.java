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

    // ★ 最新仕様：給与詳細画面用（当月の勤怠一覧）
    default List<Attendance> findByUserIdAndYearMonth(String userId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return findByUserIdAndWorkDateBetween(userId, start, end);
    }
}
