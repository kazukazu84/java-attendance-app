package com.example.sns.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sns.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByOrderByCreatedAtDesc();
    List<PostEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
