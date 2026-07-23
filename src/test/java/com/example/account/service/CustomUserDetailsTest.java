package com.example.account.service;

import static org.assertj.core.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;

class CustomUserDetailsTest {

    @Test
    @DisplayName("基本属性（ユーザー名・パスワード）の委譲取得テスト")
    void basicPropertiesTest() {
        // Given
        UserInfo user = new UserInfo();
        user.setUserId("user_01");
        user.setPassword("hashed_pass");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Then
        assertThat(userDetails.getUsername()).isEqualTo("user_01");
        assertThat(userDetails.getPassword()).isEqualTo("hashed_pass");
        
        // 固定で true を返すフラグ群の検証
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("権限生成：ROLE_ + Position名 の形式でGrantedAuthorityが作成されること")
    void getAuthoritiesTest() {
        // Given
        UserInfo user = new UserInfo();
        user.setPosition(Position.ADMIN); // 存在するPositionに合わせて変更してOK

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // When
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Then
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Nested
    @DisplayName("有効状態判定 (isEnabled)")
    class IsEnabledTest {

        @Test
        @DisplayName("isActive = 1 (在籍) の場合、isEnabledが true を返すこと")
        void isEnabled_activeUser_shouldReturnTrue() {
            // Given
            UserInfo user = new UserInfo();
            user.setIsActive(1);

            CustomUserDetails userDetails = new CustomUserDetails(user);

            // Then
            assertThat(userDetails.isEnabled()).isTrue();
        }

        @Test
        @DisplayName("isActive != 1 (無効/休職/退職など) の場合、isEnabledが false を返すこと")
        void isEnabled_inactiveUser_shouldReturnFalse() {
            // Given: 0 (無効) や 2 (休職) などのステータス
            UserInfo user0 = new UserInfo();
            user0.setIsActive(0);

            UserInfo user2 = new UserInfo();
            user2.setIsActive(2);

            // Then
            assertThat(new CustomUserDetails(user0).isEnabled()).isFalse();
            assertThat(new CustomUserDetails(user2).isEnabled()).isFalse();
        }
    }
}