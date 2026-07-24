package com.example.sns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sns.entity.QuoteRetweetEntity;

public interface QuoteRetweetRepository extends JpaRepository<QuoteRetweetEntity, Long> {

    long countByPostId(Long postId);

    boolean existsByPostIdAndUserId(Long postId, String userId);

    List<QuoteRetweetEntity> findByPostIdOrderByCreatedAtDesc(Long postId);
}
