package com.example.sns.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sns.entity.RetweetEntity;

public interface RetweetRepository extends JpaRepository<RetweetEntity, Long> {
    long countByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, String userId);
    Optional<RetweetEntity> findByPostIdAndUserId(Long postId, String userId);
}
