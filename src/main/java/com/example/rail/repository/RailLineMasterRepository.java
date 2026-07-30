package com.example.rail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rail.entity.RailLineMaster;

public interface RailLineMasterRepository extends JpaRepository<RailLineMaster, Long> {

    // railCode で検索（プロフィール → 運行状況で使用）
    Optional<RailLineMaster> findByRailCode(String railCode);

    // lineName で検索（必要なら）
    Optional<RailLineMaster> findByLineName(String lineName);
}
