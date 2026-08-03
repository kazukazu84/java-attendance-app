package com.example.rail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rail.entity.RailLineEntity;

@Repository
public interface RailLineRepository extends JpaRepository<RailLineEntity, Long> {
    Optional<RailLineEntity> findByLineName(String lineName);
}