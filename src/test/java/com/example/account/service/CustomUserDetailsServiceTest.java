package com.example.account.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.account.entity.Position;
import com.example.account.entity.UserInfo;
import com.example.account.repository.UserInfoRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService; // テスト対象

    @Mock
    private UserInfoRepository userInfoRepository;

    @Test
    @DisplayName("正常系：存在するユーザーIDを指定した場合、CustomUserDetailsが返ること")
    void loadUserByUsername_success() {
        // Given
        UserInfo user = new UserInfo();
        user.setUserId("test_user");
        user.setPassword("hashed_pass");
        user.setPosition(Position.ADMIN); // プロジェクトに実在するEnumに合わせて調整してOK
        user.setIsActive(1);

        when(userInfoRepository.findById("test_user")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test_user");

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo("test_user");
        assertThat(userDetails.getPassword()).isEqualTo("hashed_pass");

        verify(userInfoRepository, times(1)).findById("test_user");
    }

    @Test
    @DisplayName("異常系：存在しないユーザーIDを指定した場合、UsernameNotFoundExceptionが発生すること")
    void loadUserByUsername_userNotFound_shouldThrowException() {
        // Given
        when(userInfoRepository.findById("unknown_user")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("unknown_user"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("ユーザーが見つかりません");

        verify(userInfoRepository, times(1)).findById("unknown_user");
    }
}