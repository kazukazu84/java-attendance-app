package com.example.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attendance.entity.TempUserInfo;

@Repository
public interface TempUserInfoRepository extends JpaRepository<TempUserInfo, String> {
}