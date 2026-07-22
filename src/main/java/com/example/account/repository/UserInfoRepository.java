package com.example.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.account.entity.UserInfo;


//UserInfoRepository.java
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
	// 🔍 IDで部分一致検索
    List<UserInfo> findByUserIdContaining(String userId); // 💡 戻り値を UserInfo に変更

    // 🔍 名前で部分一致検索
    List<UserInfo> findByUserNameContaining(String userName); // 💡 戻り値を UserInfo に変更
}