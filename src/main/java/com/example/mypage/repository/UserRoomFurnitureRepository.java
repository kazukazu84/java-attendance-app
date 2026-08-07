package com.example.mypage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mypage.entity.UserRoomFurnitureEntity;

@Repository
public interface UserRoomFurnitureRepository extends JpaRepository<UserRoomFurnitureEntity, Long> {

    // ★ 新仕様：家具スロット順に取得（1〜6）
    List<UserRoomFurnitureEntity> findByUserIdOrderBySlotIndexAsc(String userId);

    // ★ ユーザーの家具スロットを全削除
    void deleteByUserId(String userId);
}
