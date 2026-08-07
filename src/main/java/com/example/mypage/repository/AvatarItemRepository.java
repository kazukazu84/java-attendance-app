package com.example.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.AvatarItemEntity;

@Repository
public interface AvatarItemRepository extends JpaRepository<AvatarItemEntity, Long> {}