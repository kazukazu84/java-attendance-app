package com.example.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.UserItemEntity;

@Repository
public interface UserItemRepository extends JpaRepository<UserItemEntity, Long> {
    List<UserItemEntity> findByUserIdAndItemType(String userId, String itemType);
    List<UserItemEntity> findByUserId(String userId);
}