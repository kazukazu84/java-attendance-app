package com.example.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.Attendance;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    /**
     * 指定ユーザーの指定日の勤怠取得
     */
    Optional<Attendance> findByUserIdAndWorkDate(
            String userId,
            LocalDate workDate
    );


    /**
     * ★ 管理者による強制退勤用
     *
     * clockOutが未設定（出勤中）の最新勤怠を取得
     */
    


    /**
     * 月別勤怠一覧取得
     */
    List<Attendance> findByUserIdAndWorkDateBetween(
            String userId,
            LocalDate start,
            LocalDate end
    );


    /**
     * 指定年月の勤怠一覧取得
     */
    default List<Attendance> findByUserIdAndYearMonth(
            String userId,
            int year,
            int month
    ) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return findByUserIdAndWorkDateBetween(
                userId,
                start,
                end
        );
    }


    
    Optional<Attendance> findFirstByUserIdAndClockOutIsNullOrderByWorkDateDesc(String userId);
    //最新レコード取得
    Optional<Attendance> findFirstByUserIdOrderByWorkDateDesc(String userId);
    
    
}

