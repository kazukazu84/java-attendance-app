package com.example.sns.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sns.entity.LikeEntity;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    long countByPostId(Long postId);
    boolean existsByPostIdAndUserId(Long postId, String userId);
    Optional<LikeEntity> findByPostIdAndUserId(Long postId, String userId);
}
