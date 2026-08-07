package com.example.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.GachaResultEntity;

@Repository
public interface GachaResultRepository extends JpaRepository<GachaResultEntity, Long> {
    List<GachaResultEntity> findTop10ByUserIdOrderByObtainedAtDesc(String userId);
}