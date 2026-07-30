package com.example.rail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rail.entity.DelayInfoEntity;

@Repository
public interface DelayInfoRepository extends JpaRepository<DelayInfoEntity, Long> {
    Optional<DelayInfoEntity> findFirstByRailLineLineNameOrderByOccurredAtDesc(String lineName);
}