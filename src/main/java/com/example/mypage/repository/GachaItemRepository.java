package com.example.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.GachaItemEntity;

@Repository
public interface GachaItemRepository extends JpaRepository<GachaItemEntity, Long> {
    List<GachaItemEntity> findByGachaType(String gachaType);
}