package com.example.salary.salaryconfirm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.salary.salarydetail.entity.SalaryEntity;

@Repository
public interface SalaryConfirmRepository extends JpaRepository<SalaryEntity, Integer> {

    // ★ 年別一覧（既存）
    List<SalaryEntity> findByUserInfoUserIdAndTargetYear(
            String userId,
            int targetYear
    );

    // ★ 追加：ユーザーが持っている給与データの「年一覧」を返す
    @Query("""
        SELECT DISTINCT s.targetYear
        FROM SalaryEntity s
        WHERE s.userInfo.userId = :userId
        ORDER BY s.targetYear
    """)
    List<Integer> findYearsByUserId(String userId);
}
