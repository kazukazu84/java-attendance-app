package com.example.rail.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.rail.entity.RailUserProfile;

public interface RailUserProfileRepository extends JpaRepository<RailUserProfile, Long> {

    // SpringSecurity の userId で検索
    Optional<RailUserProfile> findByUserId(String userId);
}
